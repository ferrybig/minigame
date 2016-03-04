package me.ferrybig.javacoding.minecraft.minigame.util;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FailedFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.SucceededFuture;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Fernando
 */
public class ChainedFutureTest {
	
	private EventExecutor executor;
	
	@Before
	public void before() {
		executor = (EventExecutor) Proxy.newProxyInstance(getClass().getClassLoader(), 
				new Class<?>[]{EventExecutor.class}, new InvocationHandler() {
			@Override
			@SuppressWarnings({"rawtypes", "unchecked"})
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				switch (method.getName()) {
					case "newPromise":
						return new DefaultPromise((EventExecutor)proxy);
					case "shutdownGracefully":
						return null;
					case "newSucceededFuture":
						return new SucceededFuture((EventExecutor)proxy, args[0]);
					case "newFailedFuture":
						return new FailedFuture((EventExecutor)proxy, (Throwable) args[0]);
					case "inEventLoop":
						return true;
				}
				throw new AssertionError(method);
			}
		});
	}
	
	@After
	public void after() {
		executor.shutdownGracefully();
	}
	
	@Test
	public void doesProperlyWorkWithSimpleSucceededFutureTest() throws InterruptedException, ExecutionException {
		Future<Object> succeded = executor.newSucceededFuture(this);
		ChainedFuture<Object> chained = ChainedFuture.of(executor, succeded);
		assertTrue(chained.isDone());
		assertTrue(chained.isSuccess());
		assertEquals(this, chained.get());
	}
	
	@Test
	public void doesProperlyWorkWithSimpleFailedFutureTest() {
		OutOfMemoryError error = new OutOfMemoryError();
		Future<Object> succeded = executor.newFailedFuture(error);
		ChainedFuture<Object> chained = ChainedFuture.of(executor, succeded);
		assertTrue(chained.isDone());
		assertFalse(chained.isSuccess());
		assertEquals(error, chained.cause());
	}
	
	@Test
	public void doesProperlyWorkWithDelayedSuccededFutureTest() {
		Promise<Object> promise = executor.newPromise();
		ChainedFuture<Object> chained = ChainedFuture.of(executor, promise);
		assertFalse(chained.isDone());
		promise.setSuccess(promise);
		assertTrue(chained.isDone());
		assertTrue(chained.isSuccess());
	}
	
	@Test
	public void doesProperlyWorkWithDelayedFailedFutureTest() {
		Promise<Object> promise = executor.newPromise();
		ChainedFuture<Object> chained = ChainedFuture.of(executor, promise);
		assertFalse(chained.isDone());
		promise.setFailure(new OutOfMemoryError());
		assertTrue(chained.isDone());
		assertFalse(chained.isSuccess());
	}
	
	@Test
	public void canUseSuplierConstructorTest() throws InterruptedException, ExecutionException {
		String first = "first";
		ChainedFuture<String> chained = ChainedFuture.of(executor, 
				()->executor.newSucceededFuture(first));
		
		assertTrue(chained.isDone());
		assertEquals(first, chained.get());
	}
	
	@Test
	public void canChainProperlyTest() throws InterruptedException, ExecutionException {
		String first = "first";
		String second = "second";
		String third = "third";
		
		String firstResult = first;
		String secondResult = first + second;
		String thirdResult = first + second + third;
		
		ChainedFuture<String> firstFuture = ChainedFuture.of(executor, () -> executor.newSucceededFuture(first));
		ChainedFuture<String> secondFuture = firstFuture.map(i -> executor.newSucceededFuture(i + second));
		ChainedFuture<String> thirdFuture = secondFuture.map(i -> executor.newSucceededFuture(i + third));

		assertTrue(firstFuture.isDone());
		assertEquals(firstResult, firstFuture.get());

		assertTrue(secondFuture.isDone());
		assertEquals(secondResult, secondFuture.get());

		assertTrue(thirdFuture.isDone());
		assertEquals(thirdResult, thirdFuture.get());
	}
	
	@Test
	public void canChainProperlyWithDelayedStartTest() throws InterruptedException, ExecutionException {
		String first = "first";
		String second = "second";
		String third = "third";
		
		String firstResult = first;
		String secondResult = first + second;
		String thirdResult = first + second + third;
		
		Promise<String> prom = executor.newPromise();
		ChainedFuture<String> firstFuture = ChainedFuture.of(executor, () -> prom);
		ChainedFuture<String> secondFuture = firstFuture.map(i -> executor.newSucceededFuture(i + second));
		ChainedFuture<String> thirdFuture = secondFuture.map(i -> executor.newSucceededFuture(i + third));

		assertFalse(firstFuture.isDone());
		assertFalse(secondFuture.isDone());
		assertFalse(thirdFuture.isDone());
		
		prom.setSuccess(first);
		
		assertTrue(firstFuture.isDone());
		assertEquals(firstResult, firstFuture.get());

		assertTrue(secondFuture.isDone());
		assertEquals(secondResult, secondFuture.get());

		assertTrue(thirdFuture.isDone());
		assertEquals(thirdResult, thirdFuture.get());
	}
}
