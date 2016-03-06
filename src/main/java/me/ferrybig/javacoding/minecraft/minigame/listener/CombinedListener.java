package me.ferrybig.javacoding.minecraft.minigame.listener;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;
import org.bukkit.entity.Player;

/**
 *
 * @author Fernando
 */
public class CombinedListener implements GameListener, GameListenable {
	
	private final Set<GameListener> listeners = new CopyOnWriteArraySet<>();
	
	@Override
	public boolean addListener(GameListener listener) {
		return listeners.add(listener);
	}
	
	@Override
	public boolean removeListener(GameListener listener) {
		return listeners.add(listener);
	}

	@Override
	public void gameInstanceFinished(AreaContext area) {
		listeners.forEach(l->l.gameInstanceFinished(area));
	}

	@Override
	public void gameInstanceStarted(AreaContext area) {
		listeners.forEach(l->l.gameInstanceStarted(area));
	}

	@Override
	public void playerJoinGame(AreaContext area, Player player) {
		listeners.forEach(l->l.playerJoinGame(area, player));
	}

	@Override
	public void playerLeaveGame(AreaContext area, Player player) {
		listeners.forEach(l->l.playerLeaveGame(area, player));
	}
	
	@Override
	public Collection<? extends GameListener> getListeners() {
		return listeners;
	}
	
}
