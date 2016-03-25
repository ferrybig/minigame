package me.ferrybig.javacoding.minecraft.minigame.status;

import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign.SignType;
import org.bukkit.block.Block;

public interface StatusSignManager {

	public void defineStatusSign(Block location, SignType type);

	public void removeStatusSign(Block location);
}
