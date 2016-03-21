package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.stream.Stream;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.Controller.ControllerListener;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.InformationContext;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.information.ResolvedAreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.CoreClosedException;
import me.ferrybig.javacoding.minecraft.minigame.listener.CombinedListener;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import me.ferrybig.javacoding.minecraft.minigame.util.ChainedFuture;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author Fernando
 */
public class DefaultGameCore implements GameCore {

	private final Function<Stream<Map.Entry<Area, Integer>>, Area> areaSelector;
	private final Map<String, Area> areas = new HashMap<>();
	private final Map<String, List<Future<AreaContext>>> pendingAreaContexts = new HashMap<>();
	private final Map<String, List<AreaContext>> areaContexts = new HashMap<>();
	private final Map<UUID, AreaContext> playerGames = new HashMap<>();
	private final InformationContext info;
	private final CombinedListener listeners = new CombinedListener();
	private final Promise<Object> terminationFuture;
	private final Promise<Object> startingFuture;
	private boolean running = true;
	private boolean stopped = false;
	private final EventExecutor executor;

	public DefaultGameCore(InformationContext info) {
		this(info, defaultRandomAreaSelector());
	}

	public DefaultGameCore(InformationContext info,
			Function<Stream<Map.Entry<Area, Integer>>, Area> areaSelector) {
		this.info = Objects.requireNonNull(info, "info == null");
		this.executor = Objects.requireNonNull(info.getExecutor(), "executor == null");
		this.terminationFuture = executor.newPromise();
		this.startingFuture = executor.newPromise();
		this.areaSelector = areaSelector;
		this.terminationFuture.setUncancellable();
		this.startingFuture.setUncancellable();
	}

	@Override
	public void addArea(AreaInformation ar) {
		if (isTerminating()) {
			throw new IllegalStateException("Core has been shut down");
		}
		Objects.requireNonNull(ar, "ar == null");
		Area area = this.resolvArea(info.getAreaVerifier().validate(ar));
		DefaultGameCore.this.areas.put(area.getName(), area);
	}

	@Override
	public void close() {
		if (stopped) {
			return;
		}
		running = false;
		Exception closureFailure = new CoreClosedException();
		if (!terminationFuture.isDone()) {
			terminationFuture.setSuccess(closureFailure);
		}

		Collection<Future<?>> toCancel = new ArrayList<>();
		this.pendingAreaContexts.values().forEach(toCancel::addAll);
		toCancel.forEach(f -> f.cancel(true));

		areaContexts.values().stream().flatMap(l -> new ArrayList<>(l).stream())
				.map(AreaContext::pipeline).forEach(Pipeline::terminate);
		this.info.getConfig().flushChanges();
		stopped = true;
	}

	protected AreaCreator createAreaCreator() {
		return new DefaultAreaCreator(a -> {
			Area area = resolvArea(a);
			areas.put(area.getName(), area);
			info.getConfig().saveArea(area.getName(), area).addListener(new ErrorLoggingHandler(
					"Failed to save area {0}", "Success save area {0}", area));
			return area;
		}, info.getAreaVerifier());
	}

	@Override
	public AreaCreator createArea(String name) {
		if (isTerminating()) {
			throw new IllegalStateException("Core has been shut down");
		}
		return createAreaCreator().setName(name);
	}

	@Override
	public Future<AreaContext> createRandomGameContext() {
		if (!running) {
			return info.getExecutor().newFailedFuture(new CancellationException());
		}
		checkState();
		Area randomArea = areaSelector.apply(areas.entrySet().stream()
				.filter(m -> m.getValue().canBeUsed())
				.map(e -> new AbstractMap.SimpleImmutableEntry<>(e.getValue(),
						areaContexts.getOrDefault(e.getKey(), Collections.emptyList()).size()
						+ pendingAreaContexts.getOrDefault(e.getKey(), Collections.emptyList()).size())
				));
		if (randomArea == null) {
			return executor.newFailedFuture(new NoSuchElementException("No Areas found"));
		}
		return randomArea.newInstance();
	}

	@Override
	public Future<AreaContext> createRandomGameContext(long maxDelay, TimeUnit unit) {
		checkState();
		Future<AreaContext> f = createRandomGameContext();
		this.executor.schedule(() -> f.cancel(false), maxDelay, unit);
		return f;
	}

	@Override
	public Optional<Area> getArea(String name) {
		checkState();
		return Optional.ofNullable(areas.get(name));
	}

	@Override
	public Collection<? extends AreaContext> getAreaContexts() {
		return new AbstractCollection<AreaContext>() {
			@Override
			public Stream<AreaContext> stream() {
				return areaContexts.values().stream().flatMap(List::stream);
			}

			@Override
			public Iterator<AreaContext> iterator() {
				return stream().iterator();
			}

			@Override
			public int size() {
				return areaContexts.values().stream().mapToInt(List::size).sum();
			}

		};
	}

	@Override
	public Collection<? extends Area> getAreas() {
		checkState();
		return Collections.unmodifiableCollection(this.areas.values());
	}

	@Override
	public Collection<? extends GameListener> getListeners() {
		checkState();
		return listeners.getListeners();
	}

	@Override
	public Optional<AreaContext> getGameOfPlayer(OfflinePlayer player) {
		checkState();
		Objects.requireNonNull(player, "player == null");
		return Optional.ofNullable(playerGames.get(player.getUniqueId()));
	}

	@Override
	public InformationContext getInfo() {
		return info;
	}

	@Override
	public Future<?> gracefulStop() {
		this.running = false;

		Collection<Future<?>> toCancel = new ArrayList<>();
		this.pendingAreaContexts.values().forEach(toCancel::addAll);
		toCancel.forEach(f -> f.cancel(true));

		checkStopped();

		return terminationFuture();
	}

	private void checkStopped() {
		if (this.running == false) {
			if (this.pendingAreaContexts.isEmpty() && this.areaContexts.isEmpty()) {
				if (!this.terminationFuture.isDone()) {
					this.terminationFuture.setSuccess(null);
				}
			}
		}
	}

	@Override
	public Future<?> initializeAndStart() {
		startingFuture.setSuccess(this);
		return startingFuture;
	}

	@Override
	public boolean isRunning() {
		return running == true;
	}

	@Override
	public boolean isStarted() {
		return startingFuture.isDone();
	}

	@Override
	public boolean isTerminating() {
		return running == false;
	}

	@Override
	public boolean removeArea(String area) {
		Area ar = this.areas.get(area);
		if (ar == null) {
			return false;
		}
		ChainedFuture.of(executor, () -> this.info.getConfig().saveArea(area, null))
				.addListener(new ErrorLoggingHandler(
						"Failed to remove area {0}", "Success removing area {0}", area));
		return true;
	}

	@Override
	public Future<?> startingFuture() {
		return startingFuture;
	}

	@Override
	public Future<?> terminationFuture() {
		return terminationFuture;
	}

	@Override
	public boolean addListener(GameListener listener) {
		checkState();
		return listeners.addListener(listener);
	}

	@Override
	public boolean removeListener(GameListener listener) {
		checkState();
		return listeners.removeListener(listener);
	}

	private void checkState() {
		if (terminationFuture.isDone()) {
			throw new IllegalStateException("GameCore closed");
		}
		if (!startingFuture.isDone()) {
			throw new IllegalStateException("GameCore not started");
		}
	}

	private AreaCreator editArea(Area area) {
		return this.createAreaCreator().copyInformation(area);
	}

	private Area resolvArea(ResolvedAreaInformation area) {
		return info.getAreaConstructor().construct(area, this::editArea, this::createContext);
	}

	private Future<AreaContext> createContext(Area area) {
		if (!area.canBeUsed()) {
			return executor.newFailedFuture(
					new IllegalStateException("area.canBeUsed() == false"));
		}
		AtomicReference<AreaContext> ref = new AtomicReference<>();
		Pipeline pipeline = new DefaultGamePipeline(executor);
		Controller controller = new DefaultGameController(info, pipeline.entrance());
		controller.addListener(new ControllerCommander(ref));
		Future<AreaContext> context = info.getAreaContextConstructor().construct(this, area, controller, pipeline)
				.addListener((Future<AreaContext> f) -> {
					removeAreaFuture(area, f);
					if (f.isSuccess() && f.get() != null) {
						AreaContext c = f.get();
						ref.set(c);
						areaContextCreated(c);
						c.pipeline().runLoop(c);
						c.getClosureFuture().addListener(f2 -> areaContextDestroyed(f.get()));
					}
				});
		addAreaFuture(area, context);
		if (context.isDone()) {
			removeAreaFuture(area, context);
		}
		return context.addListener(new ErrorLoggingHandler(
				"Failed to create a context for {0}", "Created context for {0}", area));
	}

	public static Function<Stream<Entry<Area, Integer>>, Area> defaultRandomAreaSelector() {
		return l -> l.min((a, b) -> a.getValue().compareTo(b.getValue())).map(Entry::getKey).orElse(null);
	}

	private void areaContextCreated(AreaContext context) {
		areaContexts.computeIfAbsent(context.getName(), i -> new ArrayList<>()).add(context);
		listeners.gameInstanceStarted(context);
	}

	private void areaContextDestroyed(AreaContext context) {
		Collection<AreaContext> l = areaContexts.get(context.getName());
		if (l != null) {
			l.remove(context);
			if (l.isEmpty()) {
				areaContexts.remove(context.getName());
			}
		}
		listeners.gameInstanceFinished(context);
		checkStopped();
	}

	private void addAreaFuture(Area main, Future<AreaContext> context) {
		List<Future<AreaContext>> l = pendingAreaContexts.get(main.getName());
		if (l == null) {
			pendingAreaContexts.put(main.getName(), l = new LinkedList<>());
		}
		l.add(context);

	}

	private void removeAreaFuture(Area main, Future<AreaContext> context) {
		Collection<Future<AreaContext>> l = pendingAreaContexts.get(main.getName());
		if (l != null) {
			l.remove(context);
			if (l.isEmpty()) {
				pendingAreaContexts.remove(main.getName());
			}
		}
		checkStopped();
	}

	private class ErrorLoggingHandler implements GenericFutureListener<Future<Object>> {

		private final Object[] args;
		private final String fail;
		private final String success;

		public ErrorLoggingHandler(String fail, String success, Object... args) {
			this.fail = fail;
			this.success = success;
			this.args = args;
		}

		@Override
		public void operationComplete(Future<Object> f) throws Exception {
			if (f.isSuccess()) {
				info.getLogger().log(Level.FINE, success, args);
			} else {
				LogRecord log = new LogRecord(Level.SEVERE, fail);
				log.setParameters(args);
				log.setThrown(f.cause());
				info.getLogger().log(log);
			}
		}
	}

	private class ControllerCommander implements ControllerListener {

		private AtomicReference<AreaContext> ref;
		private AreaContext con;

		public ControllerCommander(AtomicReference<AreaContext> ref) {
			this.ref = ref;
		}

		private AreaContext get() {
			if (con == null) {
				con = ref.get();
				if (con != null) {
					ref = null;
				}
			}
			return con;
		}

		@Override
		public boolean canAddPlayerToGame(Player player) {
			return get().equals(playerGames.get(player.getUniqueId()));
		}

		@Override
		public boolean canAddPlayerPreToGame(OfflinePlayer player) {
			return !playerGames.containsKey(player.getUniqueId());
		}

		@Override
		public void addedPlayerPreToGame(OfflinePlayer player) {
			playerGames.put(player.getUniqueId(), get());
		}

		@Override
		public void addedPlayerToGame(Player player) {
			playerGames.put(player.getUniqueId(), get());
		}

		@Override
		public void removedPlayerFromGame(Player player) {
			playerGames.put(player.getUniqueId(), get());
			listeners.playerLeaveGame(get(), player);
		}

		@Override
		public void removedPlayerFromPreGame(OfflinePlayer player) {
			playerGames.put(player.getUniqueId(), get());
		}
	}

}
