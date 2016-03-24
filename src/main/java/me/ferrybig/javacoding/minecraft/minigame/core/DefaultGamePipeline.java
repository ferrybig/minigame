package me.ferrybig.javacoding.minecraft.minigame.core;

import me.ferrybig.javacoding.minecraft.minigame.util.ExceptionRunnable;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.Phase;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.PhaseException;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;
import me.ferrybig.javacoding.minecraft.minigame.util.SafeUtil;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class DefaultGamePipeline implements Pipeline {

	private static final int STARTING_AT_FIRST_PHASE = -1;
	private static final int PIPELINE_TASK_LIMIT = 512;
	private static final int PIPELINE_COUNTER = 512;
	private final LinkedList<PhaseHolder> mainPhases = new LinkedList<>();
	private final Deque<Runnable> runQueue = new ArrayDeque<>();
	private final Deque<Runnable> pendingTasks = new ArrayDeque<>();
	private final Promise<AreaContext> terminationFuture;
	private Reference<DefaultPhaseContext> entrance = new PhantomReference<>(null, null);
	private int currPhaseIndex = -1;
	private int sizeCheckCounter = PIPELINE_COUNTER;
	private boolean killed = false;
	private AreaContext area;
	private boolean terminating = false;
	private boolean inLoop = false;
	private boolean hasFailedWithException = false;
	private Logger logger;

	public DefaultGamePipeline(EventExecutor executor) {
		terminationFuture = executor.newPromise();
		terminationFuture.setUncancellable();
		runLoop(() -> advancePhase());
	}

	@Override
	public Pipeline addFirst(Phase phase) {
		Objects.requireNonNull(phase, "phase == null");
		if(this.contains(phase))
			throw new IllegalArgumentException("Phase already added");
		PhaseHolder holder;
		mainPhases.forEach(p -> p.context.incrementCurrIndex());
		mainPhases.addFirst(holder = new PhaseHolder(phase, new DefaultPhaseContext(0)));
		if (this.currPhaseIndex > 0) {
			this.currPhaseIndex++;
			holder.shouldBeLoaded = false;
			holder.shouldBeRegistered = true;
			registerPhase(holder);
		}
		return this;
	}

	@Override
	public Pipeline addLast(Phase phase) {
		if (terminating) {
			throw new IllegalStateException("Cannot add to terminating pipeline");
		}
		Objects.requireNonNull(phase, "phase == null");
		if(this.contains(phase))
			throw new IllegalArgumentException("Phase already added");
		int newIndex = this.mainPhases.size();
		mainPhases.addLast(new PhaseHolder(phase, new DefaultPhaseContext(newIndex)));
		assert this.mainPhases.size() == newIndex + 1;
		return this;
	}

	@Override
	public boolean contains(Phase phase) {
		for(PhaseHolder p : this.mainPhases) {
			if(p.phase == phase)
				return true;
		}
		return false;
	}

	@Override
	public PhaseContext entrance() {
		DefaultPhaseContext entr = entrance.get();
		if (entr == null) {
			entrance = new SoftReference<>(entr = new DefaultPhaseContext(-1));
			entr.removed = true;
		}
		return entr;
	}

	@Override
	public Phase get(int index) {
		return this.mainPhases.get(index).phase;
	}

	@Override
	public Future<?> getClosureFuture() {
		return terminationFuture;
	}

	@Override
	public int getCurrentIndex() {
		return currPhaseIndex;
	}

	@Override
	public int indexOf(Phase phase) {
		int i = 0;
		for (PhaseHolder h : mainPhases) {
			if (Objects.equals(phase, h.phase)) {
				return i;
			}
			i++;
		}
		return i == Integer.MAX_VALUE || i == 0 ? Integer.MIN_VALUE : -i;
	}

	@Override
	public Pipeline insert(int index, Phase phase) {
		int firstTo = Math.min(index, mainPhases.size());
		int i;
		for (i = 0; i < firstTo; i++) {
			assert mainPhases.get(i).context.getCurrIndex() == i;
		}
		PhaseHolder holder;
		mainPhases.add(i, holder = new PhaseHolder(phase, new DefaultPhaseContext(i)));
		int insertedId = i;
		for (i++; i < mainPhases.size(); i++) {
			mainPhases.get(i).context.setCurrIndex(i);
		}
		if (this.currPhaseIndex > insertedId) {
			this.currPhaseIndex++;
			holder.shouldBeLoaded = false;
			holder.shouldBeRegistered = true;
			registerPhase(holder);
		}
		return this;
	}

	@Override
	public boolean isStopped() {
		return terminating;
	}

	@Override
	public boolean isStopping() {
		return terminationFuture.isDone();
	}

	private PhaseHolder getHolder(int index) {
		return this.mainPhases.size() > index && index >= 0 ? this.mainPhases.get(index) : null;
	}

	@Override
	public Pipeline replace(int index, Phase phase) {
		ListIterator<PhaseHolder> itr = mainPhases.listIterator(index);
		if (!itr.hasNext()) {
			throw new IndexOutOfBoundsException(index + " too high");
		}
		PhaseHolder old = itr.next();
		assert old.context.getCurrIndex() == index;
		PhaseHolder newHolder = new PhaseHolder(phase, new DefaultPhaseContext(index));
		newHolder.shouldBeLoaded = old.shouldBeLoaded;
		newHolder.shouldBeRegistered = old.shouldBeRegistered;
		itr.set(newHolder);
		old.shouldBeLoaded = false;
		old.shouldBeRegistered = false;
		old.context.removed = true;
		unloadPhase(old);
		unregisterPhase(old);
		registerPhase(newHolder);
		loadPhase(newHolder);
		return this;
	}

	private void loadPhase(PhaseHolder holder) {
		if (!holder.shouldBeLoaded || holder.loaded) {
			return;
		}
		runLoop(() -> {
			if (!holder.shouldBeLoaded || holder.loaded) {
				return;
			}
			logger.finest("Loading phase");
			holder.loaded = true;
			wrapWithException(() -> holder.phase.onPhaseLoad(holder.context), holder);
		});
	}

	private void unloadPhase(PhaseHolder holder) {
		if (holder.shouldBeLoaded || !holder.loaded) {
			return;
		}
		runLoop(() -> {
			if (holder.shouldBeLoaded || !holder.loaded) {
				return;
			}
			logger.finest("Unloading phase");
			holder.loaded = false;
			wrapWithException(() -> holder.phase.onPhaseUnload(holder.context), holder);
		});
	}

	private void registerPhase(PhaseHolder holder) {
		if (!holder.shouldBeRegistered || holder.registered) {
			return;
		}
		runLoop(() -> {
			if (!holder.shouldBeRegistered || holder.registered) {
				return;
			}
			logger.finest("Registering phase");
			holder.registered = true;
			wrapWithException(() -> holder.phase.onPhaseRegister(holder.context), holder);
		});
	}

	private void unregisterPhase(PhaseHolder holder) {
		if (holder.shouldBeRegistered || !holder.registered) {
			return;
		}
		runLoop(() -> {
			if (holder.shouldBeRegistered || !holder.registered) {
				return;
			}
			logger.finest("Unregistering phase");
			holder.registered = false;
			wrapWithException(() -> holder.phase.onPhaseUnregister(holder.context), holder);
		});
	}

	private void runLoop() {
		if (area == null) {
			return;
		}
		if (inLoop) {
			return;
		}
		inLoop = true;
		try {
			if (this.killed) {
				pendingTasks.clear();
				runQueue.clear();
				return;
			}
			do {
				Runnable task;
				int tasksRun = 0;
				while ((task = runQueue.poll()) != null) {
					tasksRun++;
					task.run();
					if (logger.isLoggable(Level.FINER)) {
						LogRecord record = new LogRecord(Level.FINER,
								"Ran iteration {0}, {1} tasks left and {2} pending");
						record.setParameters(new Object[]{tasksRun, runQueue.size(),
							pendingTasks.size()});
						logger.log(record);
					}
					while ((task = pendingTasks.pollLast()) != null) {
						runQueue.addFirst(task);
					}
					assert pendingTasks.isEmpty();
					pendingTasks.clear(); //reset internal state of deque
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.log(Level.FINE, "Ran {0} tasks correctly", tasksRun);
				}
				if(terminating) {
					if(currPhaseIndex < 0) {
						break;
					} else {
						decreasePhase();
					}
				}
			} while(terminating);
		} finally {
			assert inLoop == true;
			inLoop = false;
		}
	}

	private void runLoop(Runnable run) {
		if (this.sizeCheckCounter-- < 0) {
			this.sizeCheckCounter = PIPELINE_COUNTER;
			if (pendingTasks.size() + runQueue.size() > PIPELINE_TASK_LIMIT) {
				this.killed = true;
				this.terminating = true;
				this.terminationFuture.setFailure(new IllegalStateException("Pipeline crashed"));
			}
		}
		if (this.killed) {
			return;
		}
		if (inLoop) {
			this.pendingTasks.add(run);
		} else {
			runQueue.add(run);
			runLoop();
		}
	}

	@Override
	public void runLoop(AreaContext area) {
		if (area != null && this.area == null) {
			this.area = area;
			this.logger = this.area.getLogger();
			if(this.logger == null) {
				this.logger = Logger.getLogger(this.getClass().getName());
				this.logger.warning("Using own logger because none logger has been provided");
			}
		}
		runLoop();
	}

	private <T> void onException(int index, Throwable message) {
		if (hasFailedWithException) {
			logger.log(Level.SEVERE, "Caught exception in pipeline while terminating:", message);
			return;
		}
		runLoop(() -> {
			if (index >= currPhaseIndex - 1) {
				logger.log(Level.SEVERE, "Exception reached top of pipeline, terminating...", message);
				terminate();
				hasFailedWithException = true;
				return;
			}
			PhaseHolder phase = this.mainPhases.get(index + 1);
			try {
				phase.phase.exceptionCaucht(phase.context, message);
			} catch (Throwable a) {
				message.addSuppressed(a);
				logger.log(Level.SEVERE, "Caught exception in pipeline:", message);
				terminate();
				hasFailedWithException = true;
			}
		});
	}

	private void wrapWithException(ExceptionRunnable call, Object namedClass) {
		try {
			call.run();
		} catch (Throwable a) {
			if (!hasFailedWithException) {
				onException(STARTING_AT_FIRST_PHASE,
						SafeUtil.createException(s -> new PhaseException(s, a),
								"Exception calling %s", namedClass));
			}
		}
	}

	private <T> void callMethod(int index, PhaseCaller<T> callable, T message) {
		if (terminating) {
			return;
		}
		runLoop(() -> {
			int indexToCall = index + 1;
			if (indexToCall < currPhaseIndex) {
				PhaseHolder phase = this.mainPhases.get(indexToCall);
				wrapWithException(() -> callable.consume(phase.phase, phase.context, message), phase);
			}
		});
	}

	@Override
	public int size() {
		return mainPhases.size();
	}

	@Override
	public Future<?> terminate() {
		if (terminating) {
			return this.getClosureFuture();
		}
		terminating = true;
		runLoop();
		if (area == null) {
			terminationFuture.trySuccess(null);
		} else {
			area.getCore().getInfo().getExecutor().submit((Runnable) this::runLoop);
		}
		return this.getClosureFuture();
	}

	private void advancePhase() {
		PhaseHolder curr = getHolder(this.currPhaseIndex);
		if (curr != null) {
			curr.shouldBeLoaded = false;
			unloadPhase(curr);
		}
		int oldIndex = this.currPhaseIndex;
		this.currPhaseIndex++;
		runLoop(() -> {
			PhaseHolder newPhase = getHolder(this.currPhaseIndex);
			if (newPhase == null) {
				// Reached end of pipeline
				logger.fine("Reached end of pipeline");
				if (curr != null) {
					wrapWithException(() -> curr.phase.afterReset(curr.context), curr.phase);
					runLoop(() -> {
						if (oldIndex == this.currPhaseIndex) {
							curr.shouldBeLoaded = true;
							loadPhase(curr);
						}
					});
				} else {
					logger.warning("Looped through an empty pipeline, did you forget to register things?");
					terminating = true;
					terminationFuture.trySuccess(null);
				}
			} else {
				newPhase.shouldBeLoaded = true;
				newPhase.shouldBeRegistered = true;
				registerPhase(newPhase);
				loadPhase(newPhase);
			}
		});
	}

	private void decreasePhase() {
		if (this.currPhaseIndex < 0) {
			return;
		}
		PhaseHolder curr = getHolder(this.currPhaseIndex);
		curr.shouldBeLoaded = false;
		curr.shouldBeRegistered = false;
		unloadPhase(curr);
		unregisterPhase(curr);
		this.currPhaseIndex--;
		if (this.currPhaseIndex < 0) {
			terminationFuture.setSuccess(null);
			terminating = true;
		} else {
			PhaseHolder newPhase = getHolder(this.currPhaseIndex);
			assert newPhase != null; // Guarded by the if block above
			newPhase.shouldBeLoaded = true;
			wrapWithException(() -> newPhase.phase.afterReset(curr.context), curr.phase);
			runLoop(() -> {
				loadPhase(newPhase);
			});
		}
	}

	private static class PhaseHolder {

		private boolean loaded = false;
		private boolean shouldBeLoaded = false;
		private boolean registered = false;
		private boolean shouldBeRegistered = false;
		private final Phase phase;
		private final DefaultPhaseContext context;

		public PhaseHolder(Phase phase, DefaultPhaseContext context) {
			this.phase = phase;
			this.context = context;
		}

		@Override
		public String toString() {
			return "PhaseHolder{"
					+ "loaded=" + loaded
					+ ", shouldBeLoaded=" + shouldBeLoaded
					+ ", registered=" + registered
					+ ", shouldBeRegistered=" + shouldBeRegistered
					+ ", phase=" + phase + ", "
					+ "context=" + context + '}';
		}

		public boolean isLoaded() {
			return loaded;
		}

		public void setLoaded(boolean loaded) {
			this.loaded = loaded;
		}

		public boolean isRegistered() {
			return registered;
		}

		public void setRegistered(boolean registered) {
			this.registered = registered;
		}

		public boolean isShouldBeLoaded() {
			return shouldBeLoaded;
		}

		public void setShouldBeLoaded(boolean shouldBeLoaded) {
			this.shouldBeLoaded = shouldBeLoaded;
		}

		public boolean isShouldBeRegistered() {
			return shouldBeRegistered;
		}

		public void setShouldBeRegistered(boolean shouldBeRegistered) {
			this.shouldBeRegistered = shouldBeRegistered;
		}

	}

	private class DefaultPhaseContext implements PhaseContext {

		private int currIndex;
		private boolean removed = false;

		public boolean isRemoved() {
			return removed || terminating;
		}

		public DefaultPhaseContext(int currIndex) {
			this.currIndex = currIndex;
		}

		public void setCurrIndex(int newValue) {
			currIndex = newValue;
		}

		public int incrementCurrIndex() {
			return currIndex++;
		}

		public int decrementCurrIndex() {
			return currIndex--;
		}

		public int getCurrIndex() {
			return currIndex;
		}

		@Override
		public AreaContext getAreaContext() {
			return area;
		}

		@Override
		public void registerNativeListener(Listener listener) {
			if (isRemoved()) {
				throw new IllegalStateException("Cannot register when disabled");
			}
			area.getCore().getInfo().getPlugin().getServer().getPluginManager()
					.registerEvents(listener, area.getCore().getInfo().getPlugin());
		}

		@Override
		public <T> T safeCall(Callable<T> call, Object origin) {
			AtomicReference<T> o = new AtomicReference<>();
			wrapWithException(() -> o.set(call.call()), origin);
			return o.get();
		}

		@Override
		public void triggerExceptionCaucht(Throwable exception) {
			onException(currIndex, exception);
		}

		@Override
		public void triggerNextPhase() {
			if (isRemoved()) {
				return;
			}
			if (this.currIndex == -1) {
				throw new UnsupportedOperationException("Not supported.");
			}
			if (this.currIndex != currPhaseIndex) {
				throw new IllegalStateException("Cannot change phase from non-loaded phase");
			}
			advancePhase();
		}

		@Override
		public void triggerPlayerChangeTeam(PlayerTeamMessage player) {
			callMethod(currIndex, Phase::onPlayerChangeTeam, player);
			if (currIndex == -1) {
				runLoop(() -> player.getListeners().forEach(Runnable::run));
			}
		}

		@Override
		public void triggerPlayerJoin(PlayerJoinMessage player) {
			callMethod(currIndex, Phase::onPlayerJoin, player);
			if (currIndex == -1) {
				runLoop(() -> player.getListeners().forEach(Runnable::run));
			}
		}

		@Override
		public void triggerPlayerLeave(PlayerLeaveMessage player) {
			callMethod(currIndex, Phase::onPlayerLeave, player);
			if (currIndex == -1) {
				runLoop(() -> player.getListeners().forEach(Runnable::run));
			}
		}

		@Override
		public void triggerPlayerPreJoin(PlayerPreJoinMessage player) {
			callMethod(currIndex, Phase::onPlayerPreJoin, player);
			if (currIndex == -1) {
				runLoop(() -> player.getListeners().forEach(Runnable::run));
			}
		}

		@Override
		public void triggerPlayerPreLeave(PlayerPreLeaveMessage player) {
			callMethod(currIndex, Phase::onPlayerPreLeave, player);
			if (currIndex == -1) {
				runLoop(() -> player.getListeners().forEach(Runnable::run));
			}
		}

		@Override
		public void triggerPlayerSpectate(PlayerSpectateMessage player) {
			callMethod(currIndex, Phase::onPlayerSpectate, player);
			if (currIndex == -1) {
				runLoop(() -> player.getListeners().forEach(Runnable::run));
			}
		}

		@Override
		public void triggerReset() {
			if (this.currIndex == -1) {
				throw new UnsupportedOperationException("Not supported.");
			}
			if (this.currIndex != currPhaseIndex) {
				throw new IllegalStateException("Cannot change phase from non-loaded phase");
			}
			decreasePhase();
		}

		@Override
		public void triggerUserEvent(Object event) {
			callMethod(currIndex, Phase::onUserEvent, event);
		}

		@Override
		public void unregisterNativeListener(Listener listener) {
			HandlerList.unregisterAll(listener);
		}

	}

	private interface PhaseCaller<T> {

		public void consume(Phase phase, PhaseContext context, T message) throws Exception;
	}

}
