package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.logging.Level;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.Phase;
import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.PhaseException;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;
import me.ferrybig.javacoding.minecraft.minigame.util.SafeUtil;
import org.bukkit.event.Listener;

@SuppressWarnings(value = "") // TODO unsuppresses findbugs errors
public class DefaultGamePipeline implements Pipeline {

	private static final int STARTING_AT_FIRST_PHASE = -1;
	private final static int PRIORITY_EXCEPTION = 100;
	private final static int PRIORITY_USER_OBJECT = 0;
	private final static int PRIORITY_UNREGISTER = 11;
	private final static int PRIORITY_REGISTER = 10;
	private final static int PRIORITY_LOAD = 9;
	private final static int PRIORITY_UNLOAD = 21;

	private int currPhaseIndex;

	private final List<PhaseHolder> removedPhases = new ArrayList<>();
	private final LinkedList<PhaseHolder> mainPhases = new LinkedList<>();
	private final Queue<PriorityTask> runQueue = new PriorityQueue<>();
	private AreaContext area;
	private boolean terminating = false;
	private final Promise<AreaContext> terminationFuture;
	private boolean inLoop = false;

	public DefaultGamePipeline(EventExecutor executor) {
		terminationFuture = executor.newPromise();
		terminationFuture.setUncancellable();
	}

	@Override
	public Pipeline addFirst(Phase phase) {
		Objects.requireNonNull(phase, "phase == null");
		PhaseHolder holder;
		mainPhases.forEach(p-> p.context.incrementCurrIndex());
		mainPhases.addFirst(holder = new PhaseHolder(phase, new DefaultPhaseContext()));
		if(this.currPhaseIndex > 0) {
			this.currPhaseIndex++;
			holder.shouldBeLoaded = true;
			holder.shouldBeRegistered = true;
		}
		
		return this;
	}

	@Override
	public Pipeline addLast(Phase phases) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public boolean contains(Phase phase) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public PhaseContext entrance() {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public Phase get(int index) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public Future<?> getClosureFuture() {
		return terminationFuture;
	}

	@Override
	public int getCurrentIndex() {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public int indexOf(Phase phase) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public Pipeline insert(int index, Phase phase) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public boolean isStopped() {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public boolean isStopping() {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public Iterator<Phase> iterator() {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}

	@Override
	public Pipeline replace(int index, Phase phase) {
		throw new UnsupportedOperationException("Not supported yet."); // TODO
	}
	
	private void loadPhase(PhaseHolder holder) {
		if(!holder.shouldBeLoaded) {
			return;
		}
		if(holder.loaded) {
			return;
		}
		runLoop(PRIORITY_LOAD, ()->{
			if(!holder.shouldBeLoaded) {
				return;
			}
			if(holder.loaded) {
				return;
			}
			wrapWithException(()->holder.phase.onPhaseLoad(holder.context), holder);
		});
	}
	
	private void registerPhase(PhaseHolder holder) {
		
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
			Runnable task;
			while ((task = runQueue.poll()) != null) {
				task.run();
			}
		} finally {
			assert inLoop == true;
			inLoop = false;
		}
	}

	private void runLoop(int priority, Runnable run) {
		runQueue.add(new PriorityTask(run, priority));
		runLoop();
	}

	@Override
	public void runLoop(AreaContext area) {
		if (this.area == null) {
			this.area = area;
		}
		runLoop();
	}

	private <T> void onException(int index, Throwable message) {
		if (terminating) {
			return;
		}
		runLoop(PRIORITY_EXCEPTION, () -> {
			if (index >= currPhaseIndex - 1) {
				area.getCore().getInfo().getLogger()
						.log(Level.SEVERE, "Exception reached top of pipeline, terminating...", message);
				terminate();
				return;
			}
			PhaseHolder phase = this.mainPhases.get(index + 1);
			try {
				phase.phase.exceptionCaucht(phase.context, message);
			} catch (Throwable a) {
				message.addSuppressed(a);
				area.getCore().getInfo().getLogger()
						.log(Level.SEVERE, "Caught exception in pipeline:", message);
				terminate();
			}
		});
	}
	
	private void wrapWithException(ExceptionRunnable call, Object namedClass) {
		try {
			call.run();
		} catch (Throwable a) {
			onException(STARTING_AT_FIRST_PHASE, 
					SafeUtil.createException(PhaseException::new, "Exception calling %s", namedClass));
		}
	}

	private <T> void callMethod(int index, boolean reversedDirection,
			PhaseCaller<T> callable, T message) {
		if (terminating) {
			return;
		}
		runLoop(PRIORITY_USER_OBJECT, () -> {
			if (reversedDirection && index <= 0) {
				return;
			}
			if (!reversedDirection && index >= currPhaseIndex - 1) {
				return;
			}
			PhaseHolder phase = this.mainPhases.get(reversedDirection ? index - 1 : index + 1);
			wrapWithException(()->callable.consume(phase.phase, phase.context, message), phase);
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
		if (area == null) {
			terminationFuture.trySuccess(null);
		} else {
			area.getCore().getInfo().getExecutor().submit((Runnable) this::runLoop);
		}
		return this.getClosureFuture();
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
			area.getCore().getInfo().getPlugin().getServer().getPluginManager()
					.registerEvents(listener, area.getCore().getInfo().getPlugin());
		}

		@Override
		public boolean triggerExceptionCaucht(Throwable exception) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerNextPhase() {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerPlayerChangeTeam(PlayerTeamMessage player) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerPlayerJoin(PlayerJoinMessage player) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerPlayerLeave(PlayerLeaveMessage player) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerPlayerPreJoin(PlayerPreJoinMessage player) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerPlayerPreLeave(PlayerPreLeaveMessage player) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerPlayerSpectate(PlayerSpectateMessage player) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerReset() {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void triggerUserEvent(Object event) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

		@Override
		public void unregisterNativeListener(Listener listener) {
			throw new UnsupportedOperationException("Not supported yet."); // TODO
		}

	}

	private interface PhaseCaller<T> {

		public void consume(Phase phase, PhaseContext context, T message) throws Exception;
	}

	private static class PriorityTask implements Runnable, Comparable<PriorityTask> {

		private final Runnable runnable;
		private final int priority;

		public PriorityTask(Runnable run, int priority) {
			this.runnable = run;
			this.priority = priority;
		}

		@Override
		public int compareTo(PriorityTask o) {
			return Integer.compare(priority, o.priority);
		}

		public Runnable getRunnable() {
			return runnable;
		}

		public int getPriority() {
			return priority;
		}

		@Override
		public void run() {
			runnable.run();
		}

		@Override
		public int hashCode() {
			int hash = 7;
			hash = 71 * hash + Objects.hashCode(this.runnable);
			hash = 71 * hash + this.priority;
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			final PriorityTask other = (PriorityTask) obj;
			if (this.priority != other.priority) {
				return false;
			}
			if (!Objects.equals(this.runnable, other.runnable)) {
				return false;
			}
			return true;
		}

	}
	
	private interface ExceptionRunnable {
		public void run() throws Exception;
	}

}
