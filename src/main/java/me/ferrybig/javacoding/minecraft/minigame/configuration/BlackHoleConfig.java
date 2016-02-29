
package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.Collections;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import org.bukkit.block.Block;

public class BlackHoleConfig extends AbstractConfig implements FullConfig {

	public BlackHoleConfig(EventExecutor executor) {
		super(executor);
	}

	@Override
	public Future<TranslationMap> getTranslationMap() {
		return executor.newSucceededFuture(TranslationMap.getFailureMappings());
	}

	@Override
	public Future<Map<String, AreaInformation>> loadAreas() {
		return executor.newSucceededFuture(Collections.emptyMap());
	}

	@Override
	public Future<Map<Block, StatusSign>> loadSigns() {
		return executor.newSucceededFuture(Collections.emptyMap());
	}

	@Override
	public Future<?> removeArea(String name) {
		return executor.newSucceededFuture(null);
	}

	@Override
	public Future<?> removeSign(Block location) {
		return executor.newSucceededFuture(null);
	}

	@Override
	public Future<?> saveArea(String name, Area area) {
		return executor.newSucceededFuture(null);
	}

	@Override
	public Future<?> saveSign(Block location, StatusSign area) {
		return executor.newSucceededFuture(null);
	}

}
