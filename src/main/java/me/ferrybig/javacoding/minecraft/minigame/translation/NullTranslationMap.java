package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.Arrays;

class NullTranslationMap extends TranslationMap {

	@Override
	protected String getMessage(Translation key, Object[] args) {
		return key.toString() + Arrays.deepToString(args);
	}

}
