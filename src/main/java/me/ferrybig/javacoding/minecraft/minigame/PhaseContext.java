/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface PhaseContext {
	
	public AreaContext getAreaContext();

	public boolean triggerExceptionCaucht(Throwable exception);
	
	public boolean triggerPlayerPreJoin(OfflinePlayer player);

	public void triggerPlayerPreLeave(OfflinePlayer player);
	
	public boolean triggerPlayerJoin(Player player);

	public void triggerPlayerLeave(Player player);
	
	public void triggerNextPhase();
	
	public void triggerReset();
	
	public void triggerUserEvent(Object event);
}
