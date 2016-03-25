package me.ferrybig.javacoding.minecraft.minigame.listener;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import org.bukkit.entity.Player;

public interface GameListener {

	public void playerJoinGame(AreaContext area, Player player);

	public void playerLeaveGame(AreaContext area, Player player);

	public void gameInstanceFinished(AreaContext area);

	public void gameInstanceStarted(AreaContext area);

	@Deprecated
	public default GameListener combineListeners(GameListener other) {
		return new GameListener() {
			@Override
			public void gameInstanceFinished(AreaContext area) {
				GameListener.this.gameInstanceFinished(area);
				other.gameInstanceFinished(area);
			}

			@Override
			public void gameInstanceStarted(AreaContext area) {
				GameListener.this.gameInstanceStarted(area);
				other.gameInstanceStarted(area);
			}

			@Override
			public void playerJoinGame(AreaContext area, Player player) {
				GameListener.this.playerJoinGame(area, player);
				other.playerJoinGame(area, player);
			}

			@Override
			public void playerLeaveGame(AreaContext area, Player player) {
				GameListener.this.playerLeaveGame(area, player);
				other.playerLeaveGame(area, player);
			}
		};
	}

	public static GameListener addExceptionLogger(GameListener other, Logger log) {
		Objects.requireNonNull(other);
		return new GameListener() {
			@Override
			public void gameInstanceFinished(AreaContext area) {
				try {
					other.gameInstanceFinished(area);
				} catch (Throwable t) {
					log.log(Level.SEVERE, "Problem calling GameListener", t);
				}
			}

			@Override
			public void gameInstanceStarted(AreaContext area) {
				try {
					other.gameInstanceStarted(area);
				} catch (Throwable t) {
					log.log(Level.SEVERE, "Problem calling GameListener", t);
				}
			}

			@Override
			public void playerJoinGame(AreaContext area, Player player) {
				try {
					other.playerJoinGame(area, player);
				} catch (Throwable t) {
					log.log(Level.SEVERE, "Problem calling GameListener", t);
				}
			}

			@Override
			public void playerLeaveGame(AreaContext area, Player player) {
				try {
					other.playerLeaveGame(area, player);
				} catch (Throwable t) {
					log.log(Level.SEVERE, "Problem calling GameListener", t);
				}
			}
		};
	}

}
