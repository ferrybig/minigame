package me.ferrybig.javacoding.minecraft.minigame.messages;

import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage.Reason;
import org.bukkit.entity.Player;

public class PlayerLeaveMessage extends PlayerPreLeaveMessage {

	public PlayerLeaveMessage(Player player, Reason reason) {
		super(player, reason);
	}
	
	@Override
	public Player getPlayer() {
		assert super.getPlayer() instanceof Player;
		return (Player)super.getPlayer();
	}

}
