
package me.ferrybig.javacoding.minecraft.minigame.messages;

import org.bukkit.entity.Player;

public class PlayerSpectateMessage extends PlayerMessage {

	private final boolean spectating;
	
	public PlayerSpectateMessage(Player player, boolean spectating) {
		super(player);
		this.spectating = spectating;
	}

	public boolean isSpectating() {
		return spectating;
	}

}
