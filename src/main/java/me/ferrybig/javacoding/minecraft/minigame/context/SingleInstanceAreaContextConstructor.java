package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Callable;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.util.ChainedFuture;

public class SingleInstanceAreaContextConstructor
		extends FilterAreaContextConstructor {

	private final EventExecutor executor;
	private final Set<String> runningAreas = new HashSet<>();
	private final Map<String, Queue<QueueEntry>> pendingRequests = new HashMap<>();

	public SingleInstanceAreaContextConstructor(EventExecutor executor,
			AreaContextConstructor parent) {
		super(parent);
		this.executor = executor;
	}

	private Future<AreaContext> callSuper(GameCore core, Area area,
			Controller controller, Pipeline pipeline) {
		return parent.construct(core, area, controller, pipeline)
				.addListener(getListenerForArea(area.getName()));
	}

	private void triggerNextBuild(String name) {
		Queue<QueueEntry> queue = pendingRequests.get(name);
		if(queue == null) {
			runningAreas.remove(name);
			return;
		}
		QueueEntry entry;
		while ((entry = queue.poll()) != null) {
			if (entry.execute()) {
				return;
			}
		}
		assert queue.isEmpty();
		runningAreas.remove(name);
		pendingRequests.remove(name);
	}

	private GenericFutureListener<Future<AreaContext>> getListenerForArea(String name) {
		return f -> {
			if (f.isSuccess() && f.get() != null) {
				f.get().getClosureFuture().addListener(f1 -> {
					assert f1 == f.get().getClosureFuture();
					triggerNextBuild(name);
				});
			} else {
				triggerNextBuild(name);
			}
		};
	}

	@Override
	public Future<AreaContext> construct(GameCore core, Area area,
			Controller controller, Pipeline pipeline) {
		String name = area.getName();
		if (!runningAreas.contains(name)) {
			runningAreas.add(name);
			return callSuper(core, area, controller, pipeline);
		} else {
			Promise<AreaContext> c = executor.newPromise();
			pendingRequests.computeIfAbsent(name, k -> new ArrayDeque<>()).add(
					new QueueEntry(c, () -> callSuper(core, area, controller, pipeline)));
			return c;
		}
	}

	private class QueueEntry {

		private final Promise<AreaContext> promise;
		private final Callable<Future<AreaContext>> future;

		public QueueEntry(Promise<AreaContext> promise, Callable<Future<AreaContext>> future) {
			this.promise = promise;
			this.future = future;
		}

		public boolean execute() {
			if (promise.isCancelled()) {
				return false;
			}

			Future<AreaContext> result = ChainedFuture.of(executor, future);
			promise.addListener(f -> {
				assert promise == f;
				assert promise.isDone();
				if (result.isDone()) {
					return;
				}
				if (promise.isCancelled()) {
					result.cancel(false);
				}
			});
			result.addListener(f -> {
				//assert result == f;
				assert result.isDone();
				if (promise.isDone()) {
					return;
				}
				if (result.isCancelled()) {
					promise.cancel(false);
				}
				if (result.isSuccess()) {
					promise.setSuccess(result.get());
				} else {
					promise.setFailure(result.cause());
				}
			});
			return true;
		}
	}
}
