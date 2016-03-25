package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import org.bukkit.block.Block;

public interface SignConfig extends Config {

	public Future<?> removeSign(Block location);

	public Future<?> saveSign(Block location, StatusSign area);

	public Future<Map<Block, StatusSign>> loadSigns();
}
