package me.ferrybig.javacoding.minecraft.minigame.bootstrap;

import me.ferrybig.javacoding.minecraft.minigame.configuration.AreaConfiguration;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;

public interface Bootstrap {

	public Bootstrap withConfigurationProvider(AreaConfiguration conf);
	
	public Bootstrap withLeaveCommand(String name);
	
	public Bootstrap withJoinCommand(String name);
	
	public Bootstrap withGlobalGameListener(GameListener listen);
	
	
}
