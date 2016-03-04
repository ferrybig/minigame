
package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.AreaConstructor;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.bootstrap.Bootstrap;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FullConfig;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListenerAdaptor;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.plugin.Plugin;

public class DefaultBootstrap implements Bootstrap {
	
	private AreaConstructor constructor;
	private AreaVerifier areaVerifier;
	private FullConfig config;
	private GameListener listener;
	private EventExecutor executor;
	private Plugin plugin;

	@Override
	public Future<GameCore> build() {
		if(executor == null) {
			throw new IllegalStateException("executor == null");
		}
		if(constructor == null) {
			throw new IllegalStateException("constructor == null");
		}
		if(areaVerifier == null) {
			throw new IllegalStateException("areaVerifier == null");
		}
		if(config == null) {
			throw new IllegalStateException("config == null");
		}
		if(listener == null) {
			listener = new GameListenerAdaptor();
		}
		if(plugin == null) {
			throw new IllegalStateException("plugin == null");
		}
		return null;
	}

	@Override
	public Bootstrap withAreaConstructor(AreaConstructor constructor) {
		this.constructor = constructor;
		return this;
	}

	@Override
	public Bootstrap withAreaVerifier(AreaVerifier verifier) {
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
	public Bootstrap withPlugin(Plugin plugin) {
		this.plugin = plugin;
		return this;
	}

}
