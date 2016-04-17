package me.ferrybig.javacoding.minecraft.minigame.executors;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.Promise;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitEventExecutor extends AbstractScheduledEventExecutor {

	private static final int MAX_EXECUTION_DEPTH = 4;
	private final Plugin plugin;
	private int executionDepth = 0;
	private boolean shuttingDown;
	private final Promise<?> terminationFuture;
	private final BukkitTask scheduledTask;
	private final Queue<Runnable> scheduledTasks = new LinkedList<>();
	@SuppressWarnings("NonConstantLogger")
	private final Logger logger;

	public BukkitEventExecutor(Plugin plugin) {
		this(plugin, plugin.getLogger());
	}

	public BukkitEventExecutor(Plugin plugin, Logger logger) {
		this.plugin = Objects.requireNonNull(plugin);
		this.terminationFuture = super.newPromise();
		this.scheduledTask = this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
			Runnable timed;
			runLoop();
			while ((timed = pollScheduledTask()) != null) {
				timed.run();
			}
		}, 1, 1);
		this.logger = logger;
	}

	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return terminationFuture.await(timeout, unit);
	}

	private void runLoop() {
		assert inEventLoop();
		Runnable task;
		while ((task = scheduledTasks.poll()) != null) {
			task.run();
		}
	}

	private void tryRunLoop() {
		assert inEventLoop();
		if (executionDepth < MAX_EXECUTION_DEPTH) {
			executionDepth++;
			try {
				runLoop();
			} finally {
				assert executionDepth > 0;
				executionDepth--;
			}
		}
	}

	private void unsafeExecute(Runnable command) {
		if (inEventLoop()) {
			scheduledTasks.add(command);
			tryRunLoop();
		} else {
			plugin.getServer().getScheduler().runTask(plugin, command);
		}
	}

	@Override
	public void execute(Runnable command) {
		unsafeExecute(() -> {
			try {
				command.run();
			} catch (Throwable e) {
				logger.log(Level.SEVERE, "Error with task: ", e);
			}
		});
	}

	@Override
	public boolean inEventLoop() {
		return plugin.getServer().isPrimaryThread();
	}

	@Override
	public boolean inEventLoop(Thread thread) {
		if (thread.equals(Thread.currentThread())) {
			return inEventLoop();
		}
		throw new IllegalStateException("Cannot check condition");
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return tasks.stream().map(this::newTaskFor)
				.peek(this::execute).collect(Collectors.toList());
	}

	@Override
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
		return invokeAll(tasks);
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
		RunnableFuture<T> t = newTaskFor(tasks.iterator().next());
		unsafeExecute(t);
		return t.get();
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		FutureTask<T> t = new FutureTask<>(tasks.iterator().next());
		unsafeExecute(t);
		return t.get(timeout, unit);
	}

	@Override
	public boolean isShutdown() {
		return terminationFuture.isDone();
	}

	@Override
	public boolean isShuttingDown() {
		return shuttingDown;
	}

	@Override
	public boolean isTerminated() {
		return terminationFuture.isDone();
	}

	@Override
	@Deprecated
	public void shutdown() {
		shuttingDown = true;
		pollScheduledTask();
		this.scheduledTask.cancel();
		terminationFuture.setSuccess(null);
	}

	@Override
	public io.netty.util.concurrent.Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
		shutdown();
		return terminationFuture();
	}

	@Override
	public io.netty.util.concurrent.Future<?> terminationFuture() {
		return terminationFuture;
	}

	@Override
	public <V> Promise<V> newPromise() {
		Promise<V> p = super.newPromise();
		if (logger.isLoggable(Level.FINER)) {
			p.addListener(f -> {
				if (f.cause() != null) {
					if (logger.isLoggable(Level.FINEST)) {
						logger.log(Level.FINEST, "Caught exception:", f.cause());
					} else {
						logger.log(Level.FINER, "Caught exception: {0}", f.cause().toString());
					}
				}
			});
		}
		return p;
	}

}
