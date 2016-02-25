package me.ferrybig.javacoding.minecraft.minigame.phase;

import me.ferrybig.javacoding.minecraft.minigame.Phase;
import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
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
	 * Forwards the exception downstream the chain of methods.
	 * @param area
	 * @param exception
	 * @throws Exception 
	 */
	@Override
	public void exceptionCaucht(PhaseContext area, Throwable exception) throws Exception {
		area.triggerExceptionCaucht(exception);
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
