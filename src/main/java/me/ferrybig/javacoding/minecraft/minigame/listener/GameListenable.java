package me.ferrybig.javacoding.minecraft.minigame.listener;

import java.util.Collection;

/**
 *
 * @author Fernando
 */
public interface GameListenable {
	
	public Collection<? extends GameListener> getListeners();

	public boolean addListener(GameListener listener);

	public boolean removeListener(GameListener listener);

}
