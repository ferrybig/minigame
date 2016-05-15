package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class ListenerPhase extends SkippedPhase implements Listener {

	private PhaseContext area;

	protected Listener getListener() {
		return this;
	}

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

	public PhaseContext getPhaseContext() {
		return area;
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		this.area = null;
		area.unregisterNativeListener(getListener());
		super.onPhaseUnregister(area);
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		this.area = area;
		area.registerNativeListener(getListener());
		super.onPhaseRegister(area);
	}
}
