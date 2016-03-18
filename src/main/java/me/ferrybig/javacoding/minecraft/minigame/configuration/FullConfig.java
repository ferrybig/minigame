package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
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

		public Translator getTranslations();
	}
}
