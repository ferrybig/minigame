package me.ferrybig.javacoding.minecraft.minigame.phase.action;

import java.util.Optional;
import me.ferrybig.javacoding.minecraft.minigame.Controller.PlayerInfo;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.entity.Player;

public class SpectateAction extends PlayerAction {

	@Override
	protected void trigger(PhaseContext phase, Player player) {
		Optional<PlayerInfo> p = phase.getController().getPlayer(player);
		if (!p.isPresent()) {
			return;
		}
		PlayerInfo info = p.get();
		if (!info.isSpectator()) {
			info.setSpectator(true);
		}
	}

}
