package me.ferrybig.javacoding.minecraft.minigame.messages;

import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import org.bukkit.entity.Player;

public class PlayerJoinMessage extends PlayerPreJoinMessage {

	public PlayerJoinMessage(Player player) {
		super(player);
	}
	
	@Override
	public Player getPlayer() {
		assert super.getPlayer() instanceof Player;
		return (Player)super.getPlayer();
	}

}
