package me.ferrybig.javacoding.minecraft.minigame.messages;

import org.bukkit.entity.Player;

public abstract class PlayerMessage extends Message {

	protected final Player player;

	public PlayerMessage(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

}
