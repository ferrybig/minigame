
package me.ferrybig.javacoding.minecraft.minigame.phase.action;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ListenerPhase extends SkippedPhase implements Listener {

	private PhaseContext area;
	
	public boolean shouldHandle(Location loc) {
		return area.getAreaContext().isInArea(loc);
	}
	
	public boolean shouldHandle(Block loc) {
		return area.getAreaContext().isInArea(loc);
	}
	
	public boolean shouldHandle(Player loc) {
		return area.getAreaContext().isInArea(loc);
	}
	
	public boolean shouldHandle(Entity loc) {
		return area.getAreaContext().isInArea(loc.getLocation());
	}

	protected PhaseContext getPhaseContext() {
		return area;
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		this.area = null;
		area.unregisterNativeListener(this);
		super.onPhaseUnregister(area);
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		this.area = area;
		area.registerNativeListener(this);
		super.onPhaseRegister(area);
	}
}
