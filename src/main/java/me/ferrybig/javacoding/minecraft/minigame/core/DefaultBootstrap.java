
package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.AreaConstructor;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.bootstrap.Bootstrap;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FullConfig;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.plugin.Plugin;

public class DefaultBootstrap implements Bootstrap {
	
	private AreaConstructor constructor;
	private AreaVerifier areaVerifier;
	private FullConfig config;
	private GameListener listener;
	private EventExecutor executor;

	@Override
	public Future<GameCore> build() {
		if(executor == null) {
			throw new IllegalStateException("executor == null");
		}
		return null;
	}

	@Override
	public Bootstrap withAreaConstructor(AreaConstructor constructor) {
		return this;
	}

	@Override
	public Bootstrap withAreaVerifier(AreaVerifier verifier) {
		return this;
	}

	@Override
	public Bootstrap withConfig(FullConfig config) {
		return this;
	}

	@Override
	public Bootstrap withExecutor(EventExecutor executor) {
		return this;
	}

	@Override
	public Bootstrap withGlobalGameListener(GameListener listen) {
		return this;
	}

	@Override
	public Bootstrap withPlugin(Plugin plugin) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
