
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

public class EmptyConfig extends AbstractFullConfig {

	public EmptyConfig(EventExecutor executor) {
		super(executor);
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
	public Future<TranslationMap> loadTranslationMap() {
		return executor.newSucceededFuture(TranslationMap.getDefaultMappings());
	}

	@Override
	public Future<?> removeArea(String name) {
		return executor.newFailedFuture(new IllegalStateException("Does not support saving!"));
	}

	@Override
	public Future<?> removeSign(Block location) {
		return executor.newFailedFuture(new IllegalStateException("Does not support saving!"));
	}

	@Override
	public Future<?> saveSign(Block location, StatusSign area) {
		return executor.newFailedFuture(new IllegalStateException("Does not support saving!"));
	}

	@Override
	public Future<?> saveArea(String name, Area area) {
		return executor.newFailedFuture(new IllegalStateException("Does not support saving!"));
	}
	
	

}
