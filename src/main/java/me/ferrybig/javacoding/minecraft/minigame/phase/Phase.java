package me.ferrybig.javacoding.minecraft.minigame.phase;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;

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

	public void exceptionCaucht(PhaseContext area, Throwable exception) throws Exception;

	public void onUserEvent(PhaseContext area, Object userEvent) throws Exception;

	public void afterReset(PhaseContext area);

	public void onPlayerPreJoin(PhaseContext area, PlayerPreJoinMessage player) throws Exception;

	public void onPlayerPreLeave(PhaseContext area, PlayerPreLeaveMessage player) throws Exception;

	public void onPlayerJoin(PhaseContext area, PlayerJoinMessage player) throws Exception;

	public void onPlayerLeave(PhaseContext area, PlayerLeaveMessage player) throws Exception;

	public void onPlayerChangeTeam(PhaseContext area, PlayerTeamMessage player) throws Exception;

	public void onPlayerSpectate(PhaseContext area, PlayerSpectateMessage player) throws Exception;
}
