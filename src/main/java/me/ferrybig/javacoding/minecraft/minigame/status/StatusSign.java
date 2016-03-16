package me.ferrybig.javacoding.minecraft.minigame.status;

import org.bukkit.block.Block;

public interface StatusSign {

	public Block getLocation();

	public SignType getType();

	public enum SignType {
		RANDOM, FIXED_AREA
	}
}
