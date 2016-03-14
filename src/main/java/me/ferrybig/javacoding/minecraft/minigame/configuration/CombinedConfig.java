package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.util.CombinedFuture;
import org.bukkit.block.Block;

public class CombinedConfig extends AbstractFullConfig {

	private final AreaConfig areaConfig;
	private final SignConfig signConfig;
	private final TranslationConfig translationConfig;

	public CombinedConfig(AreaConfig areaConfig, SignConfig signConfig,
			TranslationConfig translationConfig, EventExecutor executor) {
		super(executor);
		this.areaConfig = Objects.requireNonNull(areaConfig, "areaConfig == null");
		this.signConfig = Objects.requireNonNull(signConfig, "signConfig == null");
		this.translationConfig = Objects.requireNonNull(translationConfig, "translationConfig == null");
	}

	@Override
	public Future<TranslationMap> loadTranslationMap() {
		return translationConfig.loadTranslationMap();
	}

	@Override
	public Future<?> removeArea(String name) {
		return areaConfig.removeArea(name);
	}

	@Override
	public Future<?> saveArea(String name, Area area) {
		return areaConfig.saveArea(name, area);
	}

	@Override
	public Future<Map<String, AreaInformation>> loadAreas() {
		return areaConfig.loadAreas();
	}

	@Override
	public Future<?> removeSign(Block location) {
		return signConfig.removeSign(location);
	}

	@Override
	public Future<?> saveSign(Block location, StatusSign area) {
		return signConfig.saveSign(location, area);
	}

	@Override
	public Future<Map<Block, StatusSign>> loadSigns() {
		return signConfig.loadSigns();
	}

	@Override
	public void close() {
		areaConfig.close();
		signConfig.close();
		translationConfig.close();
	}

	@Override
	public Future<?> flushChanges() {
		return CombinedFuture.combine(executor, Arrays.asList(areaConfig.flushChanges(),
				signConfig.flushChanges(), translationConfig.flushChanges()));
	}

}
