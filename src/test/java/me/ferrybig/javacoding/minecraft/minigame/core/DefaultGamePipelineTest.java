package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.Phase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author Fernando
 */
@SuppressWarnings("Convert2Lambda")
public class DefaultGamePipelineTest {

	private static Logger logger;

	private EventExecutor executor;
	private AreaContext context;

	@BeforeClass
	public static void beforeClas() {
		logger = Logger.getAnonymousLogger();
		logger.setLevel(Level.ALL);
		logger.setUseParentHandlers(false);
		Handler h = new ConsoleHandler();
		h.setLevel(Level.ALL);
		logger.addHandler(h);
	}

	@Before
	public void prepare() {
		executor = mock(EventExecutor.class);
		context = mock(AreaContext.class);
		when(executor.inEventLoop()).thenReturn(true);
		when(context.getLogger()).thenReturn(logger);
		doAnswer(new Answer<Promise<?>>() {
			@Override
			public Promise<?> answer(InvocationOnMock invocation) throws Throwable {
				return new DefaultPromise<>(executor);
			}
		}).when(executor).newPromise();

	}

	@Test
	public void pipelineStartsProperlyTest() throws Exception {
		Phase phase = mock(Phase.class);

		DefaultGamePipeline pipe = new DefaultGamePipeline(executor);
		pipe.addLast(phase);
		pipe.runLoop(context);

		verify(phase, times(1)).onPhaseRegister(anyObject());
		verify(phase, times(1)).onPhaseLoad(anyObject());
		verify(phase, never()).onPhaseUnregister(anyObject());
		verify(phase, never()).onPhaseUnload(anyObject());
		verify(phase, never()).afterReset(anyObject());

	}

	@Test
	public void pipelineAdvancesInRegisterTest() throws Exception {
		Phase phase = mock(Phase.class);
		Phase phase2 = mock(Phase.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, PhaseContext.class).triggerNextPhase();
				return null;
			}
		}).when(phase).onPhaseRegister(anyObject());

		DefaultGamePipeline pipe = new DefaultGamePipeline(executor);
		pipe.addLast(phase);
		pipe.addLast(phase2);
		pipe.runLoop(context);

		verify(phase, times(1)).onPhaseRegister(anyObject());
		verify(phase, never()).onPhaseLoad(anyObject());
		verify(phase, never()).onPhaseUnregister(anyObject());
		verify(phase, never()).onPhaseUnload(anyObject());
		verify(phase, never()).afterReset(anyObject());

		verify(phase2, times(1)).onPhaseRegister(anyObject());
		verify(phase2, times(1)).onPhaseLoad(anyObject());
		verify(phase2, never()).onPhaseUnregister(anyObject());
		verify(phase2, never()).onPhaseUnload(anyObject());
		verify(phase2, never()).afterReset(anyObject());
	}

	@Test
	public void pipelineAdvancesInLoadTest() throws Exception {
		Phase phase = mock(Phase.class);
		Phase phase2 = mock(Phase.class);

		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, PhaseContext.class).triggerNextPhase();
				return null;
			}
		}).when(phase).onPhaseLoad(anyObject());

		DefaultGamePipeline pipe = new DefaultGamePipeline(executor);
		pipe.addLast(phase);
		pipe.addLast(phase2);

		pipe.runLoop(context);

		verify(phase, times(1)).onPhaseRegister(anyObject());
		verify(phase, times(1)).onPhaseLoad(anyObject());
		verify(phase, never()).onPhaseUnregister(anyObject());
		verify(phase, times(1)).onPhaseUnload(anyObject());
		verify(phase, never()).afterReset(anyObject());

		verify(phase2, times(1)).onPhaseRegister(anyObject());
		verify(phase2, times(1)).onPhaseLoad(anyObject());
		verify(phase2, never()).onPhaseUnregister(anyObject());
		verify(phase2, never()).onPhaseUnload(anyObject());
		verify(phase2, never()).afterReset(anyObject());
	}

	@Test
	public void canResetPipelineFromRegisteredTest() throws Exception {
		Phase phase = mock(Phase.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, PhaseContext.class).triggerReset();
				return null;
			}
		}).when(phase).onPhaseRegister(anyObject());

		DefaultGamePipeline pipe = new DefaultGamePipeline(executor);
		pipe.addLast(phase);
		pipe.runLoop(context);

		verify(phase, times(1)).onPhaseRegister(anyObject());
		verify(phase, never()).onPhaseLoad(anyObject());
		verify(phase, times(1)).onPhaseUnregister(anyObject());
		verify(phase, never()).onPhaseUnload(anyObject());
		verify(phase, never()).afterReset(anyObject());

	}

	@Test
	public void canResetPipelineFromLoadedTest() throws Exception {
		Phase phase = mock(Phase.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, PhaseContext.class).triggerReset();
				return null;
			}
		}).when(phase).onPhaseLoad(anyObject());

		DefaultGamePipeline pipe = new DefaultGamePipeline(executor);
		pipe.addLast(phase);
		pipe.runLoop(context);

		verify(phase, times(1)).onPhaseRegister(anyObject());
		verify(phase, times(1)).onPhaseLoad(anyObject());
		verify(phase, times(1)).onPhaseUnregister(anyObject());
		verify(phase, times(1)).onPhaseUnload(anyObject());
		verify(phase, never()).afterReset(anyObject());
	}

	@Test
	public void canResetPipelineFromSecondPhaseTest() throws Exception {
		Phase phase = mock(Phase.class);
		Phase phase2 = mock(Phase.class);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, PhaseContext.class).triggerReset();
				return null;
			}
		}).when(phase2).onPhaseRegister(anyObject());
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				invocation.getArgumentAt(0, PhaseContext.class).triggerNextPhase();
				return null;
			}
		}).when(phase).onPhaseRegister(anyObject());

		DefaultGamePipeline pipe = new DefaultGamePipeline(executor);
		pipe.addLast(phase);
		pipe.addLast(phase2);
		pipe.runLoop(context);

		verify(phase, times(1)).onPhaseRegister(anyObject());
		verify(phase, times(1)).onPhaseLoad(anyObject());
		verify(phase, never()).onPhaseUnregister(anyObject());
		verify(phase, never()).onPhaseUnload(anyObject());
		verify(phase, times(1)).afterReset(anyObject());

		verify(phase2, times(1)).onPhaseRegister(anyObject());
		verify(phase2, never()).onPhaseLoad(anyObject());
		verify(phase2, times(1)).onPhaseUnregister(anyObject());
		verify(phase2, never()).onPhaseUnload(anyObject());
		verify(phase2, never()).afterReset(anyObject());

	}
}
