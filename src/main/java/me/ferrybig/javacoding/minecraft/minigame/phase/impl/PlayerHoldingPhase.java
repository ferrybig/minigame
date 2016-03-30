package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerHoldingPhase extends WaitingPhase implements Listener {

	private PhaseContext area;

	public PlayerHoldingPhase(long delayMiliseconds) {
		super(delayMiliseconds);
	}

	public PlayerHoldingPhase(long delayMiliseconds, TimeUnit unit) {
		this(unit.toMillis(delayMiliseconds));
	}

	@Override
	public void onPhaseLoad(PhaseContext area) throws Exception {
		this.area = area;
		area.registerNativeListener(this);
		super.onPhaseLoad(area);
	}

	@Override
	public void onPhaseUnload(PhaseContext area) throws Exception {
		area.unregisterNativeListener(this);
		super.onPhaseUnload(area);
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt) {
		if (evt instanceof PlayerTeleportEvent) {
			return;
		}
		if (area.getAreaContext().isInArea(evt.getPlayer())) {
			Location to = evt.getTo();
			Location from = evt.getFrom();
			if (to.getX() != from.getX() || to.getZ() != from.getZ()) {
				evt.setCancelled(true);
			}
		}
	}

}
