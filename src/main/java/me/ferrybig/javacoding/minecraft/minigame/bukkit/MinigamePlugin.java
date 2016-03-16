package me.ferrybig.javacoding.minecraft.minigame.bukkit;

import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.Bootstrap;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MinigamePlugin extends JavaPlugin implements GameCoreAccessor {

	protected final MainLoader main = new MainLoader(this) {
		@Override
		protected void initPipeline(Pipeline pipeline) {
			MinigamePlugin.this.initPipeline(pipeline);
		}

		@Override
		protected void initPlugin(Bootstrap bootstrap) {
			MinigamePlugin.this.initPlugin(bootstrap);
		}

		@Override
		protected void onGameCoreLoaded(GameCore core) {
			super.onGameCoreLoaded(core);
			MinigamePlugin.this.onGameCoreLoaded(core);
		}

		@Override
		protected void initBasicSettings(Bootstrap bootstrap) {
			super.initBasicSettings(bootstrap);
			MinigamePlugin.this.initBasicSettings(bootstrap);
		}

		@Override
		protected void onInternalUnload() {
			super.onInternalUnload();
			MinigamePlugin.this.onInternalLoad();
		}

		@Override
		protected void onInternalLoad() {
			super.onInternalLoad();
			MinigamePlugin.this.onInternalUnload();
		}

	};

	@Override
	public void onEnable() {
		super.onEnable();
		this.main.onEnable();
	}

	@Override
	public void onDisable() {
		this.main.onDisable();
		super.onDisable();
	}

	protected abstract void initPipeline(Pipeline pipeline);

	protected abstract void initPlugin(Bootstrap bootstrap);

	protected void onGameCoreLoaded(GameCore core) {
	}

	protected void initBasicSettings(Bootstrap bootstrap) {
	}

	protected void onInternalUnload() {
	}

	protected void onInternalLoad() {
	}

}
