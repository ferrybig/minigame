package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import org.bukkit.block.Block;

/**
 *
 * @author Fernando
 */
public interface FullConfig extends SignConfig, AreaConfig, TranslationConfig {

	public Future<FullyLoadedConfig> loadFully();

	public interface FullyLoadedConfig {

		public Map<Block, StatusSign> getSigns();

		public Map<String, AreaInformation> getAreas();

		public TranslationMap getTranslations();
	}
}
