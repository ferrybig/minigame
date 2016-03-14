package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.ConfigurationException;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import org.bukkit.block.Block;

public abstract class AbstractFullConfig extends AbstractConfig implements FullConfig {

	public AbstractFullConfig(EventExecutor executor) {
		super(executor);
	}

	@Override
	public Future<FullyLoadedConfig> loadFully() {
		Future<Map<String, AreaInformation>> loadAreas = loadAreas();
		Future<Map<Block, StatusSign>> loadSigns = loadSigns();
		Future<TranslationMap> translationMap = loadTranslationMap();
		Promise<FullyLoadedConfig> loaded = executor.newPromise();
		GenericFutureListener<Future<Object>> listener = (Future<Object> future) -> {
			if (loadAreas.isDone() && loadSigns.isDone() && translationMap.isDone()) {
				if (loadAreas.cause() != null || loadSigns.cause() != null || loadSigns.cause() != null) {
					ConfigurationException ex = new ConfigurationException("Could not load all database objects");
					if (loadAreas.cause() != null) {
						ex.addSuppressed(loadAreas.cause());
					}
					if (loadSigns.cause() != null) {
						ex.addSuppressed(loadSigns.cause());
					}
					if (translationMap.cause() != null) {
						ex.addSuppressed(translationMap.cause());
					}
					loaded.tryFailure(ex);
				} else {
					loaded.trySuccess(newFullyLoadedConfig(loadAreas.get(),
							loadSigns.get(), translationMap.get()));
				}
			}
		};
		loadAreas.addListener(listener);
		loadSigns.addListener(listener);
		translationMap.addListener(listener);
		return loaded;
	}

	protected FullyLoadedConfig newFullyLoadedConfig(
			Map<String, AreaInformation> areas,
			Map<Block, StatusSign> signs, TranslationMap translation) {
		return new FullyLoadedConfig() {
			@Override
			public Map<String, AreaInformation> getAreas() {
				return areas;
			}

			@Override
			public Map<Block, StatusSign> getSigns() {
				return signs;
			}

			@Override
			public TranslationMap getTranslations() {
				return translation;
			}

		};
	}

}
