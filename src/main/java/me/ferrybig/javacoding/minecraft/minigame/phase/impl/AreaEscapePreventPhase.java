
package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import io.netty.util.concurrent.ScheduledFuture;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

public class AreaEscapePreventPhase extends DefaultPhase {

	private PhaseContext area;
	private ScheduledFuture<?> future;
	private final Map<UUID, Location> lastLocations = new HashMap<>();
	private final Location locationCache = new Location(null, 0, 0, 0);

	private void checkLocations() {
		for(Player p : area.getController().getPlayers().keySet()) {
			p.getLocation(locationCache);
			boolean newLocValid = area.getAreaContext().isInArea(locationCache);
			Location playerLoc = lastLocations.get(p.getUniqueId());
			if(playerLoc == null) {
				if(newLocValid) {
					lastLocations.put(p.getUniqueId(), locationCache.clone());
				} else {
					// No solution for this problem
				}
			} else {
				if(newLocValid) {
					// Position still good
					playerLoc.setPitch(locationCache.getPitch());
					playerLoc.setYaw(locationCache.getYaw());
					playerLoc.setX(locationCache.getX());
					playerLoc.setY(locationCache.getY());
					playerLoc.setZ(locationCache.getZ());
					playerLoc.setWorld(locationCache.getWorld());
				} else {
					p.teleport(locationCache, PlayerTeleportEvent.TeleportCause.UNKNOWN);
				}
			}
		}
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		super.onPhaseUnregister(area);
		if(future != null) future.cancel(true);
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		this.area = area;
		this.future = this.area.getExecutor().scheduleWithFixedDelay(
				this::checkLocations, 0, 1, TimeUnit.SECONDS);
		area.triggerNextPhase();
	}
}
