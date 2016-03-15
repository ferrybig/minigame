package me.ferrybig.javacoding.minecraft.minigame.messages;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;

public class PlayerPreJoinMessage extends Message implements Cancellable {

	protected boolean cancelled = true;

	protected final OfflinePlayer player;

	public PlayerPreJoinMessage(OfflinePlayer player) {
		this.player = player;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}

	public OfflinePlayer getPlayer() {
		return player;
	}

}
