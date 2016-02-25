package me.ferrybig.javacoding.minecraft.minigame.phase;

import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.PlayerPhase;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;

public class DefaultPlayerPhase extends DefaultPhase implements PlayerPhase {

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
}
