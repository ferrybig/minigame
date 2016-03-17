package me.ferrybig.javacoding.minecraft.minigame.phase;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import org.bukkit.entity.Player;

public class AreaFightPhase extends DefaultPhase {

	private final Set<Player> areaPlayers = new HashSet<>();
	private boolean loaded = false;

	@Override
	public void onPlayerSpectate(PhaseContext area, PlayerSpectateMessage player) throws Exception {
		super.onPlayerSpectate(area, player);
		if (!loaded) {
			return;
		}
		if (player.isSpectating()) {
			areaPlayers.remove(player.getPlayer());
			if (areaPlayers.size() == 1) {
				area.triggerNextPhase();
			}
		}
	}

	@Override
	public void onPlayerLeave(PhaseContext area, PlayerLeaveMessage player) throws Exception {
		super.onPlayerLeave(area, player);
		if (!loaded) {
			return;
		}
		areaPlayers.remove(player.getPlayer());
		if (areaPlayers.size() == 1) {
			area.triggerNextPhase();
		}
	}

	@Override
	public void onPhaseLoad(PhaseContext area) throws Exception {
		super.onPhaseLoad(area);
		areaPlayers.clear();
		areaPlayers.addAll(area.getAreaContext().getController().getPlayers()
				.entrySet().stream().filter(e -> !e.getValue().isSpectator())
				.map(Map.Entry::getKey).collect(Collectors.toSet()));
		loaded = true;
	}

	@Override
	public void onPhaseUnload(PhaseContext area) throws Exception {
		super.onPhaseUnload(area);
		areaPlayers.clear();
		loaded = false;

	}

}
