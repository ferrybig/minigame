package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.function.Function;

public class SimpleTranslationMap extends TranslationMap {

	private final Function<? super String, String> translationFunction;

	/**
	 * Simple TranslationMap based on a function. The passed function
	 * should return null when no translation is found
	 * @param translationFunction
	 * @param parent
	 */
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
