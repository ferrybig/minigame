
package me.ferrybig.javacoding.minecraft.minigame.translation;

class NullTranslationMap extends TranslationMap {

	@Override
	protected String getMessage(Translation key) {
		return key.toString();
	}

}
