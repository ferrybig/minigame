/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame.phase;

import me.ferrybig.javacoding.minecraft.minigame.Phase;
import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class DefaultPhase implements Phase {

	
	
	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
	}
	
	@Override
	public void onPhaseLoad(PhaseContext area) throws Exception {
	}

	@Override
	public void onPhaseUnload(PhaseContext area) throws Exception {
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
	}
	
	/**
	 * Forwards the player pre join downstream using the areacontext
	 * @param area
	 * @param player
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean onPlayerPreJoin(PhaseContext area, OfflinePlayer player) throws Exception {
		return area.triggerPlayerPreJoin(player);
	}

	/**
	 * Forwards the player pre leave downstream using the areacontext
	 * @param area
	 * @param player
	 * @throws Exception 
	 */
	@Override
	public void onPlayerPreLeave(PhaseContext area, OfflinePlayer player) throws Exception {
	}

	/**
	 * Forwards the player join downstream using the areacontext
	 * @param area
	 * @param player
	 * @return
	 * @throws Exception 
	 */
	@Override
	public boolean onPlayerJoin(PhaseContext area, Player player) throws Exception {
		return area.triggerPlayerJoin(player);
	}

	@Override
	public void onPlayerLeave(PhaseContext area, Player player) throws Exception {
	}
	
	/**
	 * Forwards the exception downstream the chain of methods.
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
	 * Forwards the exception downstream the chain of methods.
	 * @param area
	 * @param userEvent
	 * @throws Exception 
	 */
	@Override
	public void onUserEvent(PhaseContext area, Object userEvent) throws Exception {
		area.triggerUserEvent(userEvent);
	}

}
