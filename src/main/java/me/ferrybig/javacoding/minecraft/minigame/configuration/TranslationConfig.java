package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;

/**
 *
 * @author Fernando
 */
public interface TranslationConfig extends Config {
	public Future<? extends Translator> loadTranslationMap();
}
