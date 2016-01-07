/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.minecraft.minigame;

import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

/**
 * This object represents a generic Phase in the execution sequence of a game
 *
 * @author Fernando
 */
public interface Phase {

	public boolean isTimedPhase(AreaContext area);

	public List<Listener> getListeners(AreaContext area);

	public boolean beforePlayerPreJoin(AreaContext area, OfflinePlayer player);

	public void onPhaseLoad(AreaContext area);

	public void onPhaseUnload(AreaContext area);

	public void onPhaseRegister(AreaContext area);

	public void onPhaseUnregister(AreaContext area);

	public void onPlayerPreJoin(AreaContext area, OfflinePlayer player);

	public void onPlayerLeave(AreaContext area, Player player);

	public void onPlayerJoin(AreaContext area, Player player);

	public void onTick(AreaContext area);

	public void onUserEvent();
}
