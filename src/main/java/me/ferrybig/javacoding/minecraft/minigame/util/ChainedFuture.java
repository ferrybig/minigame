package me.ferrybig.javacoding.minecraft.minigame.util;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.lang.ref.WeakReference;
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

	public static <T> ChainedFuture<T> of(EventExecutor executor,
			Supplier<Future<T>> futureSupplier) {
		return new ChainedFuture<>(executor, futureSupplier);
	}

	public static <T> ChainedFuture<T> of(EventExecutor executor, Future<T> future) {
		return new ChainedFuture<>(executor, future);
	}

	public static <T1, T2> ChainedFuture<T2> of(EventExecutor executor,
			Supplier<Future<T1>> futureSupplier,
			Function<? super T1, Future<T2>> map1) {
		return of(executor, futureSupplier).map(map1);
	}

	public static <T1, T2, T3> ChainedFuture<T3> of(EventExecutor executor,
			Supplier<Future<T1>> futureSupplier,
			Function<? super T1, Future<T2>> map1,
			Function<? super T2, Future<T3>> map2) {
		return of(executor, futureSupplier, map1).map(map2);
	}

	public static <T1, T2, T3, T4> ChainedFuture<T4> of(EventExecutor executor,
			Supplier<Future<T1>> futureSupplier,
			Function<? super T1, Future<T2>> map1,
			Function<? super T2, Future<T3>> map2,
			Function<? super T3, Future<T4>> map3) {
		return of(executor, futureSupplier, map1, map2).map(map3);
	}

	private final EventExecutor executor;
	private final Future<T> future;
	private final WeakReference<Future<T>> reference;

	private ChainedFuture(EventExecutor executor, Supplier<Future<T>> futureSupplier) {
		Objects.requireNonNull(futureSupplier, "futureSupplier == null");
		this.executor = Objects.requireNonNull(executor, "executor == null");
		Promise<T> prom = executor.newPromise();
		this.future = prom;
		this.reference = new WeakReference<>(future);
		try {
			Future<T> result = futureSupplier.get();
			if (result == null) {
				prom.setFailure(SafeUtil.createException(IllegalStateException::new,
						"Suplier returned null: %s", futureSupplier));
			} else {
				applyMappingOperation(result, prom);
				prom.addListener(new CancelationHandler<>(new WeakReference<>(result)));
			}
		} catch (Throwable e) {
			prom.setFailure(e);
		}

	}

	private ChainedFuture(EventExecutor executor, Future<T> future) {
		this.executor = executor;
		this.future = future;
		this.reference = new WeakReference<>(future);
	}

	@SuppressWarnings("UseSpecificCatch")
	public <O> ChainedFuture<O> map(Function<? super T, Future<O>> mapper) {
		Objects.requireNonNull(mapper, "mapper == null");
		Promise<O> prom = executor.newPromise();
		this.future.addListener((Future<T> f) -> {
			try {
				if (!f.isSuccess()) {
					if (!f.isCancelled()) {
						prom.setFailure(f.cause());
					}
					return;
				}
				Future<O> result = mapper.apply(f.get());
				if (result == null) {
					prom.setFailure(
							SafeUtil.createException(IllegalStateException::new,
									"Mapper returned null: %s", mapper));
				} else {
					applyMappingOperation(result, prom);
				}
			} catch (Throwable e) {
				prom.setFailure(e);
			}
		});
		prom.addListener(new CancelationHandler<>(this.reference));
		return new ChainedFuture<>(executor, prom);
	}

	@SuppressWarnings("UseSpecificCatch")
	public <O> ChainedFuture<O> flatMap(Function<? super T, O> mapper) {
		Objects.requireNonNull(mapper, "mapper == null");
		Promise<O> prom = executor.newPromise();
		this.future.addListener((Future<T> f) -> {
			try {
				if (!f.isSuccess()) {
					if (!f.isCancelled()) {
						prom.setFailure(f.cause());
					}
					return;
				}
				O result = mapper.apply(f.get());
				if (result == null) {
					prom.setFailure(
							SafeUtil.createException(IllegalStateException::new,
									"Mapper returned null: %s", mapper));
				} else {
					prom.setSuccess(result);
				}
			} catch (Throwable e) {
				prom.setFailure(e);
			}
		});
		prom.addListener(new CancelationHandler<>(this.reference));
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

	private static <T> void applyMappingOperation(Future<T> in, Promise<? super T> out) {
		if (in.isDone()) {
			if (in.isSuccess()) {
				try {
					out.setSuccess(in.get());
				} catch (InterruptedException | ExecutionException ex) {
					out.setFailure(ex);
				}
			} else {
				out.setFailure(in.cause());
			}
		} else {
			in.addListener((Future<T> f) -> {
				if (f.isSuccess()) {
					out.setSuccess(f.get());
				} else if (!f.isCancelled()) {
					out.setFailure(f.cause());
				}
			});
		}
	}

	private static class CancelationHandler<T, R> implements GenericFutureListener<Future<T>> {

		private final WeakReference<? extends Future<R>> ref;

		public CancelationHandler(WeakReference<? extends Future<R>> ref) {
			this.ref = ref;
		}

		@Override
		public void operationComplete(Future<T> f) throws Exception {
			if (f.isCancelled()) {
				Future<R> r = ref.get();
				if (r != null) {
					r.cancel(true);
				}
			}
		}
	}

}
