/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import java.util.concurrent.ExecutorService;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface PhaseContext {
	
	public AreaContext getAreaContext();

	public boolean triggerExceptionCaucht(Throwable exception);
	
	public boolean triggerPlayerPreJoin(OfflinePlayer player);

	/**
	 * @deprecated Automaticly moved downstream
	 * @param player
	 * @deprecated
	 */
	@Deprecated
	public void triggerPlayerPreLeave(OfflinePlayer player);
	
	public boolean triggerPlayerJoin(Player player);

	/**
	 * @deprecated Automaticly moved downstream
	 * @param player
	 * @deprecated
	 */
	@Deprecated
	public void triggerPlayerLeave(Player player);
	
	public void triggerNextPhase();
	
	public void triggerReset();
	
	public void triggerUserEvent(Object event);
	
	public default ExecutorService getExecutor() {
		return this.getAreaContext().getExecutor();
	}
	
	public void registerNativeListener(Listener listener);
	
	public void unregisterNativeListener(Listener listener);
}
