package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.AreaConstructor;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContextConstructor;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Bootstrap;
import me.ferrybig.javacoding.minecraft.minigame.configuration.EmptyConfig;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FullConfig;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListenerAdaptor;
import me.ferrybig.javacoding.minecraft.minigame.util.ChainedFuture;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.TranslateableAreaVerrifer;
import org.bukkit.plugin.Plugin;

public class DefaultBootstrap implements Bootstrap {

	private AreaConstructor constructor;
	private TranslateableAreaVerrifer areaVerifier;
	private AreaContextConstructor areaContextConstructor;
	private FullConfig config;
	private GameListener listener;
	private EventExecutor executor;
	private Plugin plugin;
	private Logger logger;

	@Override
	public Future<GameCore> build() {
		if (executor == null) {
			throw new IllegalStateException("executor == null");
		}
		if (constructor == null) {
			throw new IllegalStateException("constructor == null");
		}
		if (areaVerifier == null) {
			throw new IllegalStateException("areaVerifier == null");
		}
		if (config == null) {
			config = new EmptyConfig(executor);
		}
		if (listener == null) {
			listener = new GameListenerAdaptor();
		}
		if (plugin == null) {
			throw new IllegalStateException("plugin == null");
		}
		if (logger == null) {
			logger = plugin.getLogger();
		}
		return ChainedFuture.of(executor, config::loadFully,
				c -> executor.submit(() -> {
					GameCore core = new DefaultGameCore(new DefaultInformationContext(constructor,
							areaContextConstructor, areaVerifier.unwrap(c.getTranslations()),
							executor, logger,
							plugin, c.getTranslations(), config, c.getSigns()));
					c.getAreas().values().stream().forEach(core::addArea);
					return core;
				}));
	}

	@Override
	public Bootstrap withAreaConstructor(AreaConstructor constructor) {
		this.constructor = constructor;
		return this;
	}

	@Override
	public Bootstrap withAreaContextConstructor(AreaContextConstructor areaContextConstructor) {
		this.areaContextConstructor = areaContextConstructor;
		return this;
	}

	@Override
	public Bootstrap withAreaVerifier(TranslateableAreaVerrifer verifier) {
		this.areaVerifier = verifier;
		return this;
	}

	@Override
	public Bootstrap withConfig(FullConfig config) {
		this.config = config;
		return this;
	}

	@Override
	public Bootstrap withExecutor(EventExecutor executor) {
		this.executor = executor;
		return this;
	}

	@Override
	public Bootstrap withGlobalGameListener(GameListener listen) {
		this.listener = listen;
		return this;
	}

	@Override
	public Bootstrap withLogger(Logger logger) {
		this.logger = logger;
		return this;
	}

	@Override
	public Bootstrap withPlugin(Plugin plugin) {
		this.plugin = plugin;
		return this;
	}

}
