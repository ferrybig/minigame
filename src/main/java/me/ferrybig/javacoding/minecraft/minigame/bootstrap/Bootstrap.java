package me.ferrybig.javacoding.minecraft.minigame.bootstrap;

import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import me.ferrybig.javacoding.minecraft.minigame.configuration.AreaConfig;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FullConfig;

public interface Bootstrap {

	public Bootstrap withConfigurationProvider(AreaConfig conf);
	
	public Bootstrap withLeaveCommand(String name);
	
	public Bootstrap withJoinCommand(String name);
	
	public Bootstrap withGlobalGameListener(GameListener listen);
	
	public Bootstrap withConfig(FullConfig config);
	
	public Bootstrap withAreaVerifier(AreaVerifier verifier);
	
	public GameCore build();
	
}
