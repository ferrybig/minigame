
package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.Collections;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import org.bukkit.block.Block;

public class BlackHoleConfig extends AbstractConfig implements SignConfig, AreaConfig {

	private final Future<?> successed;

	public BlackHoleConfig(EventExecutor executor) {
		super(executor);
		successed = executor.newSucceededFuture(null);
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
		return successed;
	}

	@Override
	public Future<?> removeSign(Block location) {
		return successed;
	}

	@Override
	public Future<?> saveArea(String name, Area area) {
		return successed;
	}

	@Override
	public Future<?> saveSign(Block location, StatusSign area) {
		return successed;
	}

}
