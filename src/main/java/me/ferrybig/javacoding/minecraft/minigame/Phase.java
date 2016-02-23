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

	public void onPhaseLoad(PhaseContext area) throws Exception;

	public void onPhaseUnload(PhaseContext area) throws Exception;

	public void onPhaseRegister(PhaseContext area) throws Exception;

	public void onPhaseUnregister(PhaseContext area) throws Exception;

	public boolean exceptionCaucht(PhaseContext area, Throwable exception) throws Exception;

	public boolean onPlayerPreJoin(PhaseContext area, OfflinePlayer player) throws Exception;

	public void onPlayerPreLeave(PhaseContext area, OfflinePlayer player) throws Exception;

	public boolean onPlayerJoin(PhaseContext area, Player player) throws Exception;

	public void onPlayerLeave(PhaseContext area, Player player) throws Exception;

	public void onUserEvent(PhaseContext area, Object userEvent) throws Exception;
}
