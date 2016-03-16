package me.ferrybig.javacoding.minecraft.minigame.status;

public interface StatusSign {

	public SignType getType();

	public enum SignType {
		RANDOM, FIXED_AREA
	}
}
