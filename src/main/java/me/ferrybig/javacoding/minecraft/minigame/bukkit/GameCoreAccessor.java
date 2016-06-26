package me.ferrybig.javacoding.minecraft.minigame.bukkit;

import java.util.Optional;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;

/**
 *
 * @author Fernando
 */
public interface GameCoreAccessor {

	public boolean isLoaded();

	public GameCore getCore();

	public default Optional<GameCore> asOptional() {
		return isLoaded() ? Optional.of(getCore()) : Optional.empty();
	}
}
