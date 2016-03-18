package me.ferrybig.javacoding.minecraft.minigame.messages;

import org.bukkit.entity.Player;

public class PlayerTeamMessage extends PlayerMessage {

	private final String team;

	public PlayerTeamMessage(Player player, String team) {
		super(player);
		this.team = team;
	}

	public String getTeam() {
		return team;
	}

}
