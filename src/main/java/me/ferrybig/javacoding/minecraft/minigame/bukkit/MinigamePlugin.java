
package me.ferrybig.javacoding.minecraft.minigame.bukkit;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.bootstrap.Bootstrap;
import me.ferrybig.javacoding.minecraft.minigame.core.DefaultBootstrap;
import me.ferrybig.javacoding.minecraft.minigame.executors.BukkitEventExecutor;
import me.ferrybig.javacoding.minecraft.minigame.util.ChainedFuture;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MinigamePlugin extends JavaPlugin {

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
		super.onDisable();
	}
	
	public void onInternalLoad() {
		gameCoreLoader = ChainedFuture.of(executor, ()->{
			Bootstrap b = new DefaultBootstrap();
			this.initBasicSettings(b);
			return executor.newSucceededFuture(b);
		}).map(Bootstrap::build);
	}
	
	public void onInternalUnload() {
		if(gameCore != null) {
			gameCore.close();
		} else if (gameCoreLoader != null) {
			
		}
	}
	
	protected void initBasicSettings(Bootstrap bootstrap) {
		bootstrap.withPlugin(this).withExecutor(executor);
		initPlugin(bootstrap);
	}
	
	protected abstract void initPlugin(Bootstrap bootstrap);
	
	protected abstract void initPipeline(Pipeline pipeline);

}
