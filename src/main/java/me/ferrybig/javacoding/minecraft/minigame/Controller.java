package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.AttributeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage.Reason;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Controller {

	public void addListener(ControllerListener listener);

	public void removeListener(ControllerListener listener);

	public default void kickAll() {
		for(OfflinePlayer player : new ArrayList<>(this.getAllPlayers().keySet())) {
			this.removePlayer(player);
		}
	}

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

		public default boolean canAddPlayerToGame(Player player) {
			return true;
		}

		public default boolean canAddPlayerPreToGame(OfflinePlayer player) {
			return true;
		}

		public default void addedPlayerPreToGame(OfflinePlayer player) {

		}

		public default void addedPlayerToGame(Player player) {
		}

		public default void removedPlayerFromGame(Player player) {
		}

		public default void removedPlayerFromPreGame(OfflinePlayer player) {
		}

		public default void playerSpectatorStateChanged(Player player) {
		}

		public default void playerTeamStateChanged(Player player) {
		}
	}

}
