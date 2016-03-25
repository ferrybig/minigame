package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.AttributeMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage.Reason;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Controller {

	public void addListener(ControllerListener listener);

	public void removeListener(ControllerListener listener);

	public void kickAll();

	public boolean tryAddPlayer(List<? extends OfflinePlayer> player);

	public boolean addPlayer(Player player);

	public default void removePlayer(OfflinePlayer player) {
		removePlayer(player, Reason.AUTOMATIC_LEAVE);
	}

	public void removePlayer(OfflinePlayer player, Reason reason);

	public Map<Player, PlayerInfo> getPlayers();

	public Map<OfflinePlayer, PlayerInfo> getAllPlayers();

	public Optional<PlayerInfo> getPlayer(OfflinePlayer player);

	public default boolean isInArea(OfflinePlayer player) {
		return getPlayer(player).isPresent();
	}

	public interface PlayerInfo extends AttributeMap {

		public OfflinePlayer getOfflinePlayer();

		public Player getPlayer();

		public Optional<String> getTeam();

		public boolean isFullyJoined();

		public boolean isSpectator();

		public void setTeam(String team);

		public void setSpectator(boolean spectator);
	}

	public interface ControllerListener {

		public boolean canAddPlayerToGame(Player player);

		public boolean canAddPlayerPreToGame(OfflinePlayer player);

		public void addedPlayerPreToGame(OfflinePlayer player);

		public void addedPlayerToGame(Player player);

		public void removedPlayerFromGame(Player player);

		public void removedPlayerFromPreGame(OfflinePlayer player);
	}

}
