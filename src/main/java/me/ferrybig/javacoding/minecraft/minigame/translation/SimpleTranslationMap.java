
package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.function.Function;

public class SimpleTranslationMap extends TranslationMap {

	private final Function<? super String, String> translationFunction;

	public SimpleTranslationMap(Function<? super String, String> translationFunction,
			TranslationMap parent) {
		super(parent);
		this.translationFunction = translationFunction;
	}

	@Override
	protected String getMessage(Translation key, Object[] arguments) {
		return replaceArgs(this.translationFunction.apply(key.key()), arguments);
	}

}
