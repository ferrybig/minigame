package me.ferrybig.javacoding.minecraft.minigame.messages;

import io.netty.util.Recycler;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

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
