
package me.ferrybig.javacoding.minecraft.minigame.bukkit;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import me.ferrybig.javacoding.minecraft.minigame.Bootstrap;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FileConfig;
import me.ferrybig.javacoding.minecraft.minigame.context.SingleInstanceAreaContextConstructor;
import me.ferrybig.javacoding.minecraft.minigame.core.DefaultArea;
import me.ferrybig.javacoding.minecraft.minigame.core.DefaultAreaContext;
import me.ferrybig.javacoding.minecraft.minigame.core.DefaultBootstrap;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.MinigameException;
import me.ferrybig.javacoding.minecraft.minigame.executors.BukkitEventExecutor;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.util.ChainedFuture;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.plugin.Plugin;

public abstract class MainLoader implements GameCoreAccessor {
	private final Plugin plugin;
	private EventExecutor executor;
	private GameCore gameCore;
	private Future<GameCore> gameCoreLoader;

	public MainLoader(Plugin plugin) {
		this.plugin = plugin;
	}

	public void onEnable() {
		executor = new BukkitEventExecutor(plugin);
		onInternalLoad();
	}

	public void onDisable() {
		onInternalUnload();
		executor.shutdownGracefully();
	}

	protected void onInternalLoad() {
		gameCoreLoader = ChainedFuture.of(executor, () -> {
			Bootstrap b = new DefaultBootstrap();
			this.initBasicSettings(b);
			return executor.newSucceededFuture(b);
		}).map(Bootstrap::build);
		gameCoreLoader.addListener((Future<GameCore> c) -> {
			if(c.isSuccess()) {
				gameCore = c.get();
				onGameCoreLoaded(gameCore);
			} else {
				onFailure(c.isCancelled() ? new CancellationException() :
						new MinigameException(c.cause()));
			}
		});
	}

	protected void onFailure(Throwable cause) {
	}

	protected void onInternalUnload() {
		if (gameCoreLoader != null) {
			gameCoreLoader = null;
		}
		if (gameCore != null) {
			gameCore.close();
			gameCore = null;
		}
	}

	protected void initBasicSettings(Bootstrap bootstrap) {
		bootstrap.withPlugin(plugin);
		bootstrap.withLogger(plugin.getLogger());
		bootstrap.withExecutor(executor);
		bootstrap.withAreaVerifier(new AreaVerifier() {
			@Override
			public List<String> getProblems(AreaInformation area) {
				return Collections.emptyList();
			}

			@Override
			public Set<String> getValidTeams(AreaInformation area) {
				return Collections.emptySet();
			}

			@Override
			public boolean isCorrect(AreaInformation area) {
				return true;
			}
		});
		bootstrap.withAreaContextConstructor(new SingleInstanceAreaContextConstructor(
				executor, DefaultAreaContext.factory(executor, this::initPipeline)));
		bootstrap.withAreaConstructor(DefaultArea::new);
		bootstrap.withConfig(new FileConfig(executor,
				new File(plugin.getDataFolder(), "data.yml"), plugin.getServer()));
		initPlugin(bootstrap);
	}

	protected abstract void initPlugin(Bootstrap bootstrap);

	protected abstract void initPipeline(Pipeline pipeline);

	@Override
	public boolean isLoaded() {
		return gameCore != null;
	}

	@Override
	public GameCore getCore() {
		if (!isLoaded()) {
			throw new IllegalStateException("Game core not loaded yet");
		}
		return gameCore;
	}

	protected void onGameCoreLoaded(GameCore core) {
	}
}
