package me.ferrybig.javacoding.minecraft.minigame.phase;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;

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

	@Override
	public void afterReset(PhaseContext area) {
		area.triggerReset();
	}

	@Override
	public void onPlayerChangeTeam(PhaseContext area, PlayerTeamMessage player) throws Exception {
		area.triggerPlayerChangeTeam(player);
	}

	/**
	 * Forwards the player pre join downstream using the areacontext
	 *
	 * @param area
	 * @param player
	 * @throws Exception
	 */
	@Override
	public void onPlayerPreJoin(PhaseContext area, PlayerPreJoinMessage player) throws Exception {
		area.triggerPlayerPreJoin(player);
	}

	/**
	 * Forwards the player pre leave downstream using the areacontext
	 *
	 * @param area
	 * @param player
	 * @throws Exception
	 */
	@Override
	public void onPlayerPreLeave(PhaseContext area, PlayerPreLeaveMessage player) throws Exception {
		area.triggerPlayerPreLeave(player);
	}

	/**
	 * Forwards the player join downstream using the areacontext
	 *
	 * @param area
	 * @param player
	 * @throws Exception
	 */
	@Override
	public void onPlayerJoin(PhaseContext area, PlayerJoinMessage player) throws Exception {
		area.triggerPlayerJoin(player);
	}

	/**
	 * Forwards the player join downstream using the areacontext
	 *
	 * @param area
	 * @param player
	 * @throws Exception
	 */
	@Override
	public void onPlayerLeave(PhaseContext area, PlayerLeaveMessage player) throws Exception {
		area.triggerPlayerLeave(player);
	}

	@Override
	public void onPlayerSpectate(PhaseContext area, PlayerSpectateMessage player) throws Exception {
		area.triggerPlayerSpectate(player);
	}

}
