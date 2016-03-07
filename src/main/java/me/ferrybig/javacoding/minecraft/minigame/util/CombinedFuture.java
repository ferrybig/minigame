package me.ferrybig.javacoding.minecraft.minigame.util;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

public class CombinedFuture {

	public <T, Z extends Collection<? extends Future<?>>> Future<T>
			combine(EventExecutor executor, Z futures, Function<Z, T> returnValue) {
		Objects.requireNonNull(executor, "executor == null");
		Objects.requireNonNull(futures, "futures == null");
		Objects.requireNonNull(returnValue, "returnValue == null");
		Promise<T> promise = executor.newPromise();
		AtomicInteger doneTasks = new AtomicInteger(futures.size());
		AtomicReference<Throwable> ref = new AtomicReference<>();
		GenericFutureListener<Future<Object>> listener = f -> {
			Throwable cause = f.cause();
			if (cause != null) {
				Throwable oldEx;
				Throwable newEx;
				do {
					oldEx = ref.get();
					if (oldEx == null) {
						newEx = new IllegalStateException("1 or more operations failed", cause);
					} else {
						(newEx = oldEx).addSuppressed(cause);
					}
				} while (ref.compareAndSet(oldEx, newEx));
			}
			if (doneTasks.decrementAndGet() > 0) {
				return;
			}
			assert doneTasks.get() == 0;
			if (ref.get() == null) {
				try {
					promise.setSuccess(returnValue.apply(futures));
				} catch (Throwable e) {
					promise.setFailure(e);
				}
			} else {
				promise.setFailure(ref.get());
			}
		};
		futures.stream().forEach(f -> f.addListener(listener));
		return promise;
	}

	public Future<?> combine(EventExecutor executor, Collection<? extends Future<?>> futures) {
		return combine(executor, futures, f -> null);
	}

	public <T> Future<T> combine(EventExecutor executor,
			Collection<? extends Future<?>> futures, Supplier<T> returnValue) {
		return combine(executor, futures, f -> returnValue.get());
	}
}
