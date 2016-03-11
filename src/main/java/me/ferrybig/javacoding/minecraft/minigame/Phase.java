package me.ferrybig.javacoding.minecraft.minigame;

import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
}
