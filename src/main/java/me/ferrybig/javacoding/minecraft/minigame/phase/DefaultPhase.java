/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame.phase;

import java.util.ArrayList;
import java.util.List;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.Phase;
import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DefaultPhase implements Phase {

	/**
	 * Forwards the exception back up the chain of methods.
	 * @param area
	 * @param exception
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean exceptionCaucht(PhaseContext area, Throwable exception) throws Exception {
		return area.triggerExceptionCaucht(exception);
	}

	/**
	 * Should this area recieve random tick updates
	 * @param area
	 * @return 
	 */
	@Override
	public boolean isTimedPhase(PhaseContext area) {
		return false;
	}

	@Override
	public void onPlayerLeaveJoin(PhaseContext area, OfflinePlayer player) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Called before a player joins
	 * @param area
	 * @param player
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean onPlayerPreJoin(AreaContext area, OfflinePlayer player) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onPhaseLoad(PhaseContext area) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onPhaseUnload(PhaseContext area) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean onPlayerJoin(PhaseContext area, Player player) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onPlayerLeave(PhaseContext area, Player player) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onTick(PhaseContext area) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void onUserEvent(Object userEvent) throws Exception {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	

}
