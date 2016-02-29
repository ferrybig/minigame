
package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;

public class DefaultTranslationConfig extends AbstractConfig implements TranslationConfig {

	public DefaultTranslationConfig(EventExecutor executor) {
		super(executor);
	}

	@Override
	public Future<TranslationMap> getTranslationMap() {
		return executor.newSucceededFuture(TranslationMap.getDefaultMappings());
	}

}
