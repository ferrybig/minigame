package me.ferrybig.javacoding.minecraft.minigame.phase.action;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeAction extends PlayerAction {

	private final GameMode newMode;

	public GameModeAction(GameMode newMode) {
		this.newMode = newMode;
	}

	@Override
	protected void trigger(PhaseContext phase, Player player) {
		player.setGameMode(newMode);
	}

}
