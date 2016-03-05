package me.ferrybig.javacoding.minecraft.minigame.bukkit;

import me.ferrybig.javacoding.minecraft.minigame.GameCore;

/**
 *
 * @author Fernando
 */
public interface GameCoreAccessor {
	public boolean isLoaded();
	
	public GameCore getCore();
}
