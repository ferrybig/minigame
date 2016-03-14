
package me.ferrybig.javacoding.minecraft.minigame.bukkit;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.bootstrap.Bootstrap;
import me.ferrybig.javacoding.minecraft.minigame.core.DefaultBootstrap;
import me.ferrybig.javacoding.minecraft.minigame.executors.BukkitEventExecutor;
import me.ferrybig.javacoding.minecraft.minigame.util.ChainedFuture;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MinigamePlugin extends JavaPlugin implements GameCoreAccessor {

	private EventExecutor executor;
	private GameCore gameCore;
	private Future<GameCore> gameCoreLoader;
	
	@Override
	public void onEnable() {
		super.onEnable();
		executor = new BukkitEventExecutor(this);
		onInternalLoad();
	}

	@Override
	public void onDisable() {
		onInternalUnload();
		executor.shutdownGracefully();
		super.onDisable();
	}
	
	protected void onInternalLoad() {
		gameCoreLoader = ChainedFuture.of(executor, ()->{
			Bootstrap b = new DefaultBootstrap();
			this.initBasicSettings(b);
			return executor.newSucceededFuture(b);
		}).map(Bootstrap::build);
	}
	
	protected void onInternalUnload() {
		if (gameCoreLoader != null) {
			gameCoreLoader = null;
		}
		if(gameCore != null) {
			gameCore.close();
			gameCore = null;
		}
		
	}
	
	protected void initBasicSettings(Bootstrap bootstrap) {
		bootstrap.withPlugin(this);
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
		if(!isLoaded())
			throw new IllegalStateException();
		return gameCore;
	}
}
