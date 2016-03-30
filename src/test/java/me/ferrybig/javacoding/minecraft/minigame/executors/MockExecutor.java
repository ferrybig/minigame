package me.ferrybig.javacoding.minecraft.minigame.executors;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.SucceededFuture;
import java.util.concurrent.Callable;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.RETURNS_SMART_NULLS;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockExecutor {

	public static EventExecutor getExecutor() {
		EventExecutor executor = mock(EventExecutor.class, RETURNS_SMART_NULLS);
		when(executor.inEventLoop()).thenReturn(true);
		doAnswer(invocation -> {
			try {
				return new SucceededFuture<>(executor,
						invocation.getArgumentAt(0, Callable.class).call());
			} catch (Throwable ex) {
				return new FailedFuture<>(executor, ex);
			}

		}).when(executor).submit((Callable<?>) anyObject());
		doAnswer(invocation -> new DefaultPromise<>(executor)).when(executor).newPromise();

		doAnswer(invocation -> new SucceededFuture<>(executor,
				invocation.getArgumentAt(0, Object.class)))
				.when(executor).newSucceededFuture(anyObject());

		doAnswer(invocation -> new FailedFuture<>(executor,
				invocation.getArgumentAt(0, Throwable.class)))
				.when(executor).newFailedFuture(anyObject());

		return executor;
	}
}
