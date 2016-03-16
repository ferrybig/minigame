package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.EventExecutor;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.AreaConstructor;
import me.ferrybig.javacoding.minecraft.minigame.InformationContext;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FullConfig;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContextConstructor;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;

public class DefaultInformationContext implements InformationContext {

	private final AreaConstructor areaConstructor;
	private final AreaContextConstructor areaContextConstructor;
	private final AreaVerifier areaVerifier;
	private final EventExecutor eventExecutor;
	private final Logger logger;
	private final Plugin plugin;
	private final Map<Block, StatusSign> signs;
	private final TranslationMap translationMap;
	private final FullConfig config;

	public DefaultInformationContext(AreaConstructor areaConstructor,
			AreaContextConstructor areaContextConstructor, AreaVerifier areaVerifier,
			EventExecutor eventExecutor, Logger logger, Plugin plugin,
			TranslationMap translationMap, FullConfig config, Map<Block, StatusSign> signs) {
		this.areaConstructor = Objects.requireNonNull(areaConstructor, "areaConstructor == null");
		this.areaContextConstructor = Objects.requireNonNull(areaContextConstructor, "areaContextConstructor == null");
		this.areaVerifier = Objects.requireNonNull(areaVerifier, "areaVerifier == null");
		this.eventExecutor = Objects.requireNonNull(eventExecutor, "eventExecutor == null");
		this.logger = Objects.requireNonNull(logger, "logger == null");
		this.plugin = Objects.requireNonNull(plugin, "plugin == null");
		this.translationMap = Objects.requireNonNull(translationMap, "translationMap == null");
		this.config = Objects.requireNonNull(config, "config == null");
		this.signs = signs;
	}

	@Override
	public AreaConstructor getAreaConstructor() {
		return areaConstructor;
	}

	@Override
	public AreaContextConstructor getAreaContextConstructor() {
		return areaContextConstructor;
	}

	@Override
	public AreaVerifier getAreaVerifier() {
		return areaVerifier;
	}

	@Override
	public FullConfig getConfig() {
		return config;
	}

	@Override
	public EventExecutor getExecutor() {
		return eventExecutor;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	@Override
	public TranslationMap getTranslations() {
		return translationMap;
	}

	@Override
	public Map<Block, StatusSign> getSigns() {
		return signs;
	}

}
