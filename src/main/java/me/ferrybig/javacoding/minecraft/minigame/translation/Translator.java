package me.ferrybig.javacoding.minecraft.minigame.translation;

public interface Translator {

	public static final Object[] CACHED_EMPTY_ARRAY = new Object[0];

	public default String translate(Translation translation) {
		return this.translate(translation, CACHED_EMPTY_ARRAY);
	}

	public String translate(Translation translation, Object... args);

}
