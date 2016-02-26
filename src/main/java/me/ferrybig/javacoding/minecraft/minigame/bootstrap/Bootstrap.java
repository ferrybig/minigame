package me.ferrybig.javacoding.minecraft.minigame.bootstrap;

import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import me.ferrybig.javacoding.minecraft.minigame.configuration.AreaConfig;

public interface Bootstrap {

	public Bootstrap withConfigurationProvider(AreaConfig conf);
	
	public Bootstrap withLeaveCommand(String name);
	
	public Bootstrap withJoinCommand(String name);
	
	public Bootstrap withGlobalGameListener(GameListener listen);
	
	
}
