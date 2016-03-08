package me.ferrybig.javacoding.minecraft.minigame.messages;

import org.bukkit.OfflinePlayer;

public class PlayerPreLeaveMessage extends Message {

	protected final OfflinePlayer player;
	protected final Reason reason;

	public PlayerPreLeaveMessage(OfflinePlayer player, Reason reason) {
		this.player = player;
		this.reason = reason;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

	public enum Reason {
		PLAYER_QUIT_SERVER,
		MANUAL_LEAVE
	}

}
