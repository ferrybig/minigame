package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;

public class DefaultTranslationConfig extends AbstractConfig implements TranslationConfig {

	public DefaultTranslationConfig(EventExecutor executor) {
		super(executor);
	}

	@Override
	public Future<? extends Translator> loadTranslationMap() {
		return executor.newSucceededFuture(TranslationMap.getDefaultMappings());
	}

}
