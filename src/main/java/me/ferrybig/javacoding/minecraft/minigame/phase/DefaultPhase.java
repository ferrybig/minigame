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
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DefaultPhase implements Phase {

	@Override
	public boolean beforePlayerPreJoin(AreaContext area, OfflinePlayer player) {
		return true;
	}

	@Override
	public List<Listener> getListeners(AreaContext area) {
		return new ArrayList<>();
	}

	@Override
	public boolean isTimedPhase(AreaContext area) {
		return false;
	}

	@Override
	public void onPhaseLoad(AreaContext area) {
		
	}

	@Override
	public void onPhaseRegister(AreaContext area) {
		
	}

	@Override
	public void onPhaseUnload(AreaContext area) {
		
	}

	@Override
	public void onPhaseUnregister(AreaContext area) {
		
	}

	@Override
	public void onPlayerJoin(AreaContext area, Player player) {
		
	}

	@Override
	public void onPlayerLeave(AreaContext area, Player player) {
		
	}

	@Override
	public void onPlayerPreJoin(AreaContext area, OfflinePlayer player) {
		
	}

	@Override
	public void onTick(AreaContext area) {
		
	}

	@Override
	public void onUserEvent() {
		
	}

}
