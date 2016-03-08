package me.ferrybig.javacoding.minecraft.minigame;

import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;

public interface Triggerable {

	boolean triggerExceptionCaucht(Throwable exception);

	void triggerNextPhase();

	void triggerPlayerChangeTeam(PlayerTeamMessage player);

	void triggerPlayerJoin(PlayerJoinMessage player);

	void triggerPlayerLeave(PlayerLeaveMessage player);

	void triggerPlayerPreJoin(PlayerPreJoinMessage player);

	void triggerPlayerPreLeave(PlayerPreLeaveMessage player);

	void triggerPlayerSpectate(PlayerSpectateMessage player);

	void triggerReset();

	void triggerUserEvent(Object event);

}
