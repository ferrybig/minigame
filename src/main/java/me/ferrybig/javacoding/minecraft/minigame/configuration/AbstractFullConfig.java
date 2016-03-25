package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.ConfigurationException;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.block.Block;

public abstract class AbstractFullConfig extends AbstractConfig implements FullConfig {

	public AbstractFullConfig(EventExecutor executor) {
		super(executor);
	}

	@Override
	public Future<FullyLoadedConfig> loadFully() {
		Future<Map<String, AreaInformation>> loadAreas = loadAreas();
		Future<Map<Block, StatusSign>> loadSigns = loadSigns();
		Future<? extends Translator> translationMap = loadTranslationMap();
		Promise<FullyLoadedConfig> loaded = executor.newPromise();
		GenericFutureListener<Future<Object>> listener = (Future<Object> future) -> {
			if (loadAreas.isDone() && loadSigns.isDone() && translationMap.isDone()) {
				Throwable areaCause = loadAreas.cause();
				Throwable loadSignCause = loadSigns.cause();
				Throwable translationCause = translationMap.cause();
				if (areaCause != null || loadSignCause != null || translationCause != null) {
					Throwable mainCause = null;
					ConfigurationException ex = new ConfigurationException(
							"Could not load all database objects");
					if (areaCause != null) {
						ex.initCause(areaCause);
						mainCause = areaCause;
					}
					if (loadSignCause != null) {
						if(mainCause != null)
							ex.addSuppressed(loadSignCause);
						else {
							ex.initCause(loadSignCause);
							mainCause = loadSignCause;
						}
					}
					if (translationCause != null) {
						if(mainCause != null)
							ex.addSuppressed(translationCause);
						else {
							ex.initCause(translationCause);
						}
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
			Map<Block, StatusSign> signs, Translator translation) {
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
			public Translator getTranslations() {
				return translation;
			}

		};
	}

}
