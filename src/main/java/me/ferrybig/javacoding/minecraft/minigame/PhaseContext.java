package me.ferrybig.javacoding.minecraft.minigame;

import java.util.concurrent.ExecutorService;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface PhaseContext {
	
	public AreaContext getAreaContext();

	public boolean triggerExceptionCaucht(Throwable exception);
	
	public void triggerPlayerPreJoin(PlayerPreJoinMessage player);

	public void triggerPlayerPreLeave(PlayerPreLeaveMessage player);
	
	public void triggerPlayerJoin(PlayerJoinMessage player);

	public void triggerPlayerLeave(PlayerLeaveMessage player);
	
	public void triggerNextPhase();
	
	public void triggerReset();
	
	public void triggerUserEvent(Object event);
	
	public default ExecutorService getExecutor() {
		return this.getAreaContext().getExecutor();
	}
	
	public void registerNativeListener(Listener listener);
	
	public void unregisterNativeListener(Listener listener);
}
