package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;

/**
 *
 * @author Fernando
 */
public interface TranslationConfig extends Config {
	public Future<TranslationMap> loadTranslationMap();
}
