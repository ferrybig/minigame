package me.ferrybig.javacoding.minecraft.minigame.translation;

/**
 *
 * @author Fernando
 */
public interface Translation {

	public String key();

	public default void checkArguments(Object ... args) {
	}
}
