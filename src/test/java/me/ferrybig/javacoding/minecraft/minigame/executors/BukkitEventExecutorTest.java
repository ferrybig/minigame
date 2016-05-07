package me.ferrybig.javacoding.minecraft.minigame.executors;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;

/**
 *
 * @author Fernando
 */
public class BukkitEventExecutorTest {

	@Mock
	private Plugin plugin;
	@Mock
	private Server server;
	@Mock
	private BukkitScheduler schedular;
	@Mock
	private BukkitTask id;

	@Before
	public void before() {
		MockitoAnnotations.initMocks(this);
		when(plugin.getServer()).thenReturn(server);
		when(server.getScheduler()).thenReturn(schedular);
		when(server.isPrimaryThread()).thenReturn(true);
		when(schedular.runTaskTimer(anyObject(), (Runnable) anyObject(), anyInt(), anyInt())).thenReturn(id);
		when(schedular.runTask(null, (Runnable) null)).thenAnswer((InvocationOnMock invocation) -> {
			assertEquals(plugin, invocation.getArgumentAt(0, Plugin.class));
			invocation.getArgumentAt(1, Runnable.class).run();
			return null;
		});
	}

	@Test
	public void taskProperlyDisabledOnShutdownTest() {
		BukkitEventExecutor executor = new BukkitEventExecutor(plugin);
		executor.shutdownGracefully();
		verify(id).cancel();
	}

	@Test
	@SuppressWarnings("ResultOfObjectAllocationIgnored")
	public void taskProperlyRegisteredOnStartupTest() {
		new BukkitEventExecutor(plugin);
		verify(schedular).runTaskTimer(anyObject(), (Runnable) anyObject(), anyInt(), anyInt());
	}

	@Test
	public void incomingTasksAreExecutedTest() {
		BukkitEventExecutor executor = new BukkitEventExecutor(plugin);
		Runnable mock = mock(Runnable.class);
		executor.execute(mock);
		verify(mock).run();
	}

	@Test
	public void executorChecksProperlyMainThreadTest() {
		BukkitEventExecutor executor = new BukkitEventExecutor(plugin);
		assertTrue(executor.inEventLoop());
		verify(server, atLeastOnce()).isPrimaryThread();
	}
}
