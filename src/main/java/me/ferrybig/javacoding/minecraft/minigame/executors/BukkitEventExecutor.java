package me.ferrybig.javacoding.minecraft.minigame.executors;

import io.netty.util.concurrent.AbstractScheduledEventExecutor;
import io.netty.util.concurrent.Promise;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BukkitEventExecutor extends AbstractScheduledEventExecutor {

	private final Plugin plugin;
	private int executionDepth = 0;
	private boolean shuttingDown;
	private final Promise<?> terminationFuture;
	private final BukkitTask task;

	public BukkitEventExecutor(Plugin plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.terminationFuture = super.newPromise();
		this.task = this.plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
			Runnable timed;
			while((timed = pollScheduledTask())!= null)
				timed.run();
		}, 1, 1);
	}
	
	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return terminationFuture.await(timeout, unit);
	}

	@Override
	public void execute(Runnable command) {
		if(inEventLoop() && executionDepth < 4) {
			executionDepth++;
			try {
				command.run();
			} finally {
				assert executionDepth > 0;
				executionDepth--;
			}
			return;
		}
		plugin.getServer().getScheduler().runTask(plugin, command);
	}

	@Override
	public boolean inEventLoop() {
		return plugin.getServer().isPrimaryThread();
	}

	@Override
	public boolean inEventLoop(Thread thread) {
		if(thread.equals(Thread.currentThread())) {
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
		if(inEventLoop()) {
			try {
				t.run();
			} catch(Throwable e) {
				throw new ExecutionException(e);
			}
		} else {
			execute(t);
		}
		return t.get();
	}

	@Override
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		FutureTask<T> t = new FutureTask<>(tasks.iterator().next());
		if(inEventLoop()) {
		    try {
				t.run();
			} catch(Throwable e) {
				throw new ExecutionException(e);
			}
		} else {
			execute(t);
		}
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
		this.task.cancel();
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

	
}
