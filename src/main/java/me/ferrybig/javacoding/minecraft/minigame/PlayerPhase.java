package me.ferrybig.javacoding.minecraft.minigame;

import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;

public interface PlayerPhase extends Phase {
	public void onPlayerPreJoin(PhaseContext area, PlayerPreJoinMessage player) throws Exception;

	public void onPlayerPreLeave(PhaseContext area, PlayerPreLeaveMessage player) throws Exception;

	public void onPlayerJoin(PhaseContext area, PlayerJoinMessage player) throws Exception;

	public void onPlayerLeave(PhaseContext area, PlayerLeaveMessage player) throws Exception;
	
	public void onPlayerChangeTeam(PhaseContext area, PlayerTeamMessage player) throws Exception;

	public void onPlayerSpectate(PhaseContext area, PlayerSpectateMessage player) throws Exception;
}
