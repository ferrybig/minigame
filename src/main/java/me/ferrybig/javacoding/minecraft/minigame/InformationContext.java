package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.EventExecutor;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FullConfig;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContextConstructor;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.plugin.Plugin;

public interface InformationContext {

	/**
	 * Get the translationmap, this map contains the defined translations
	 * @return the translationmap
	 */
	public TranslationMap getTranslations();

	/**
	 * Gets the logger used for this instance
	 * @return the global logger
	 */
	public Logger getLogger();

	/**
	 * Gets the global event executor. This executor is used for scheduling of
	 * tasks that will be run later. The executor may choose to directly execute
	 * a passed in task, instead of waiting until the current method call is
	 * complete
	 * @return The global event executor
	 */
	public EventExecutor getExecutor();

	/**
	 * Gets the area constructor
	 * @return the area constructor
	 */
	public AreaConstructor getAreaConstructor();

	/**
	 * Gets the area context constructor. This constructor may impose limits on
	 * the created area's, such as max 1 area for every defined area, or a
	 * maxium limit of 1 area constructor at the same time.
	 * @return the area constructor
	 */
	public AreaContextConstructor getAreaContextConstructor();

	/**
	 * Gets the area verrifier. The job of a area verrifier is to verify a area,
	 * and returns the valid teams for the area.
	 * @return the area verrifier
	 */
	public AreaVerifier getAreaVerifier();

	/**
	 * Gets the plugin responsable for this information context.
	 * @return the plugin
	 */
	public Plugin getPlugin();

	/**
	 * Gets the configuration for this information context
	 * @return the config
	 */
	public FullConfig getConfig();

}
