package me.ferrybig.javacoding.minecraft.minigame.configuration;

import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.ConfigurationException;

public interface AreaConfiguration {

	public void remove(String name);
	
	public void save(String name, Area area);
	
	public Map<String, Area> loadAll() throws ConfigurationException;
}
