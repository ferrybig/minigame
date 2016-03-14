package me.ferrybig.javacoding.minecraft.minigame.listener;

import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import org.bukkit.entity.Player;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Assume;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 *
 * @author Fernando
 */
public class CombinedListenerTest {
	
	@Test
	public void removalEmptyTest() {
		CombinedListener listener = new CombinedListener();
		GameListener l1 = mock(GameListener.class);
		assertFalse(listener.removeListener(l1));
	}
	
	@Test
	public void removalOtherInstanceTest() {
		CombinedListener listener = new CombinedListener();
		GameListener l1 = mock(GameListener.class);
		GameListener l2 = mock(GameListener.class);
		listener.addListener(l1);
		assertFalse(listener.removeListener(l2));
	}
	
	@Test
	public void AddPreventsDublicateInstancesTest() {
		CombinedListener listener = new CombinedListener();
		GameListener l1 = mock(GameListener.class);
		assertTrue(listener.addListener(l1));
		assertFalse(listener.addListener(l1));
	}
	
	@Test
	public void callsDelegatedProperlyTest() {
		CombinedListener listener = new CombinedListener();
		GameListener l1 = mock(GameListener.class);
		AreaContext areaContext = mock(AreaContext.class);
		Player player = mock(Player.class);
		
		Assume.assumeTrue(listener.addListener(l1));
		
		listener.gameInstanceFinished(areaContext);
		listener.gameInstanceStarted(areaContext);
		listener.playerJoinGame(areaContext, player);
		listener.playerLeaveGame(areaContext, player);
		
		verify(l1).gameInstanceFinished(areaContext);
		verify(l1).gameInstanceStarted(areaContext);
		verify(l1).playerJoinGame(areaContext, player);
		verify(l1).playerLeaveGame(areaContext, player);
	}
	
	@Test
	public void callsNotDelegatedAfterRemoveTest() {
		CombinedListener listener = new CombinedListener();
		GameListener l1 = mock(GameListener.class);
		AreaContext areaContext = mock(AreaContext.class);
		Player player = mock(Player.class);
		
		Assume.assumeTrue(listener.addListener(l1));
		Assume.assumeTrue(listener.removeListener(l1));
		
		listener.gameInstanceFinished(areaContext);
		listener.gameInstanceStarted(areaContext);
		listener.playerJoinGame(areaContext, player);
		listener.playerLeaveGame(areaContext, player);
		
		verify(l1, never()).gameInstanceFinished(areaContext);
		verify(l1, never()).gameInstanceStarted(areaContext);
		verify(l1, never()).playerJoinGame(areaContext, player);
		verify(l1, never()).playerLeaveGame(areaContext, player);
	}
}
