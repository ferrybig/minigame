package me.ferrybig.javacoding.minecraft.minigame.util;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 *
 * @author Fernando
 * @param <T>
 */
public class ChainedFuture<T> implements Future<T> {

	public static <T> ChainedFuture<T> of(
			EventExecutor executor, Supplier<Future<T>> futureSupplier) {
		return new ChainedFuture<>(executor, futureSupplier);
	}

	private static <T> ChainedFuture<T> of(EventExecutor executor, Future<T> future) {
		return new ChainedFuture<>(executor, future);
	}

	private final EventExecutor executor;
	private final Future<T> future;
	
	private ChainedFuture(EventExecutor executor, Supplier<Future<T>> futureSupplier) {
		Objects.requireNonNull(futureSupplier, "futureSupplier == null");
		this.executor = Objects.requireNonNull(executor, "executor == null");
		Promise<T> prom = executor.newPromise();
		this.future = prom;
		try {
			Future<T> result = futureSupplier.get();
			if(result == null) {
				prom.setFailure(new IllegalArgumentException("Supplier returned null " + 
						futureSupplier));
			} else {
				result.addListener((Future<T> f) -> {
					if(f.isSuccess()) {
						prom.setSuccess(f.get());
					} else {
						prom.setFailure(f.cause());
					}
				});
			}
		} catch (Throwable e) {
			prom.setFailure(e);
		}
	}
	
	private ChainedFuture(EventExecutor executor, Future<T> future) {
		this.executor = executor;
		this.future = future;
	}
	
	@SuppressWarnings("UseSpecificCatch")
	public <O> ChainedFuture<O> map(Function<T, Future<O>> mapper) {
		Objects.requireNonNull(mapper, "mapper == null");
		Promise<O> prom = executor.newPromise();
		this.future.addListener((Future<T> f) -> {
			try {
				if(!f.isSuccess()) {
					prom.setFailure(f.cause());
					return;
				}
				Future<O> result = mapper.apply(f.get());
				if(result == null) {
					prom.setFailure(
							SafeUtil.createException(IllegalStateException::new, 
							"Mapper returned null", mapper));
				} else {
					result.addListener((Future<O> f1) -> {
						if(f1.isSuccess()) {
							prom.setSuccess(f1.get());
						} else {
							prom.setFailure(f1.cause());
						}
					});
				}
			} catch (Throwable e) {
				prom.setFailure(e);
			}
		});
		return new ChainedFuture<>(executor, prom);
	}

	@Override
	public boolean isSuccess() {
		return future.isSuccess();
	}

	@Override
	public boolean isCancellable() {
		return future.isCancellable();
	}

	@Override
	public Throwable cause() {
		return future.cause();
	}

	@Override
	public Future<T> addListener(GenericFutureListener<? extends Future<? super T>> gl) {
		return future.addListener(gl);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Future<T> addListeners(GenericFutureListener<? extends Future<? super T>>... gls) {
		return future.addListeners(gls);
	}

	@Override
	public Future<T> removeListener(GenericFutureListener<? extends Future<? super T>> gl) {
		return future.removeListener(gl);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Future<T> removeListeners(GenericFutureListener<? extends Future<? super T>>... gls) {
		return future.removeListeners(gls);
	}

	@Override
	public Future<T> sync() throws InterruptedException {
		return future.sync();
	}

	@Override
	public Future<T> syncUninterruptibly() {
		return future.syncUninterruptibly();
	}

	@Override
	public Future<T> await() throws InterruptedException {
		return future.await();
	}

	@Override
	public Future<T> awaitUninterruptibly() {
		return future.awaitUninterruptibly();
	}

	@Override
	public boolean await(long l, TimeUnit tu) throws InterruptedException {
		return future.await(l, tu);
	}

	@Override
	public boolean await(long l) throws InterruptedException {
		return future.await(l);
	}

	@Override
	public boolean awaitUninterruptibly(long l, TimeUnit tu) {
		return future.awaitUninterruptibly(l, tu);
	}

	@Override
	public boolean awaitUninterruptibly(long l) {
		return future.awaitUninterruptibly(l);
	}

	@Override
	public T getNow() {
		return future.getNow();
	}

	@Override
	public boolean cancel(boolean bln) {
		return future.cancel(bln);
	}

	@Override
	public boolean isCancelled() {
		return future.isCancelled();
	}

	@Override
	public boolean isDone() {
		return future.isDone();
	}

	@Override
	public T get() throws InterruptedException, ExecutionException {
		return future.get();
	}

	@Override
	public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		return future.get(timeout, unit);
	}
	
	
}
