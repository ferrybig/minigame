/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import java.util.List;
import java.util.Set;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * This object represents a generic Phase in the execution sequence of a game
 * 
 * @author Fernando
 */
public abstract class Phase {
	public abstract Set<PhaseTrigger> getTiggers();
	
	public abstract List<Listener> getListeners(AreaContext area);
	
	public abstract boolean beforePlayerPreJoin(OfflinePlayer player);
	
	public abstract void onPlayerPreJoin(OfflinePlayer player);
	
	public abstract void onPlayerLeave(Player player);
	
	public abstract void onPlayerJoin(Player player);
	
	public abstract void onTick();
}
