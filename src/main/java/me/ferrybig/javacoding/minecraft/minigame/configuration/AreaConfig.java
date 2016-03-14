package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;

public interface AreaConfig extends Config {

	public Future<?> removeArea(String name);
	
	public Future<?> saveArea(String name, Area area);
	
	public Future<Map<String, AreaInformation>> loadAreas();
}
