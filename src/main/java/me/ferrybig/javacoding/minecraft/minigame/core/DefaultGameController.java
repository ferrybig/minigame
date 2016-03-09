package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.DefaultAttributeMap;
import java.lang.ref.Reference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.InformationContext;
import me.ferrybig.javacoding.minecraft.minigame.Triggerable;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DefaultGameController implements Controller {

	private final InformationContext info;
	private final Triggerable trigger;
	private final Predicate<OfflinePlayer> beforePreJoin;
	private final Predicate<Player> beforeJoin;
	private final Consumer<Player> afterJoin;
	private final Consumer<OfflinePlayer> afterPreJoin;
	private final Consumer<Player> afterLeave;
	private final Set<UUID> offlinePlayers = new HashSet<>();
	private final Map<UUID, PlayerInfo> onlinePlayers = new HashMap<>();

	public DefaultGameController(InformationContext info, Triggerable trigger, Predicate<OfflinePlayer> beforePreJoin, Predicate<Player> beforeJoin, Consumer<Player> afterJoin, Consumer<OfflinePlayer> afterPreJoin, Consumer<Player> afterLeave) {
		this.info = info;
		this.trigger = trigger;
		this.beforePreJoin = beforePreJoin;
		this.beforeJoin = beforeJoin;
		this.afterJoin = afterJoin;
		this.afterPreJoin = afterPreJoin;
		this.afterLeave = afterLeave;
	}

	@Override
	public boolean addPlayer(Player player) {
		if (!offlinePlayers.contains(player.getUniqueId())) {
			if (beforePreJoin.test(player)) {
				offlinePlayers.add(player.getUniqueId());
			} else {
				return false;
			}
		}
		PlayerJoinMessage join = new PlayerJoinMessage(player);
		trigger.triggerPlayerJoin(join);
		return join.isCancelled();
	}

	@Override
	public Map<OfflinePlayer, PlayerInfo> getAllPlayers() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Optional<PlayerInfo> getPlayer(OfflinePlayer player) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Map<Player, PlayerInfo> getPlayers() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void removePlayer(OfflinePlayer player) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean tryAddPlayer(List<? extends OfflinePlayer> players) {
		Objects.requireNonNull(players, "players == null");
		players = players.stream().filter(Objects::nonNull).collect(Collectors.toList());
		boolean failed = false;
		int seen = 0;
		int total = players.size();
		try {
			Iterator<? extends OfflinePlayer> it = players.iterator();
			while (it.hasNext()) {
				OfflinePlayer p = it.next();
				if (p == null) {
					throw new IllegalArgumentException("players.contains(null) == true");
				}
				if (this.offlinePlayers.contains(p.getUniqueId())) {
					continue;
				}
				if (!this.beforePreJoin.test(p)) {
					failed = true;
					break;
				}
				offlinePlayers.add(p.getUniqueId());
				PlayerPreJoinMessage pl = new PlayerPreJoinMessage(p);
				this.trigger.triggerPlayerPreJoin(pl);
				// This method will automatic call the listeners
				if (pl.isCancelled()) {
					failed = true;
					break;
				}
				seen++;
			}
		} finally {
			if (failed || seen != total) {
				players.forEach(this::removePlayer);
			}
		}
		return failed;
	}

	private static class PlayerQuitListener implements Listener {

		private final Reference<DefaultGameController> ref;

		public PlayerQuitListener(Reference<DefaultGameController> ref) {
			this.ref = ref;
		}

		@EventHandler
		public void onQuit(PlayerQuitEvent quit) {
			DefaultGameController game = ref.get();
			if (game == null) {
				HandlerList.unregisterAll(this);
			} else {
				game.removePlayer(quit.getPlayer());
			}
		}
	}

	private class DefaultPlayerInfo extends DefaultAttributeMap implements PlayerInfo {

		private String team;
		private boolean spectator;

		@Override
		public OfflinePlayer getOfflinePlayer() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Player getPlayer() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public Optional<String> getTeam() {
			return team == null ? Optional.empty() : Optional.of(team);
		}

		@Override
		public void setTeam(String team) {
			if (Objects.equals(this.team, team)) {
				return;
			}
			trigger.triggerPlayerChangeTeam(new PlayerTeamMessage(getPlayer(), team));
			this.team = team;
		}

		@Override
		public boolean isFullyJoined() {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		@Override
		public boolean isSpectator() {
			return spectator;
		}

		@Override
		public void setSpectator(boolean spectator) {
			if (this.spectator == spectator) {
				return;
			}
			trigger.triggerPlayerSpectate(new PlayerSpectateMessage(getPlayer(), spectator));
			this.spectator = spectator;
		}

	}
}
