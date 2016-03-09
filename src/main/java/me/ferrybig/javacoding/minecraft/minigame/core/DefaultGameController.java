package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.DefaultAttributeMap;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.InformationContext;
import me.ferrybig.javacoding.minecraft.minigame.Triggerable;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage.Reason;
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
	private final Consumer<OfflinePlayer> afterPreLeave;
	private final Map<UUID, DefaultPlayerInfo> playerStates = new HashMap<>();

	public DefaultGameController(InformationContext info, Triggerable trigger,
			Predicate<OfflinePlayer> beforePreJoin, Predicate<Player> beforeJoin,
			Consumer<Player> afterJoin, Consumer<OfflinePlayer> afterPreJoin,
			Consumer<Player> afterLeave, Consumer<OfflinePlayer> afterPreLeave) {
		this.info = Objects.requireNonNull(info, "info == null");
		this.trigger = Objects.requireNonNull(trigger, "trigger == null");
		this.beforePreJoin = Objects.requireNonNull(beforePreJoin, "beforePreJoin == null");
		this.beforeJoin = Objects.requireNonNull(beforeJoin, "beforeJoin == null");
		this.afterJoin = Objects.requireNonNull(afterJoin, "afterJoin == null");
		this.afterPreJoin = Objects.requireNonNull(afterPreJoin, "afterPreJoin == null");
		this.afterLeave = Objects.requireNonNull(afterLeave, "afterLeave == null");
		this.afterPreLeave = Objects.requireNonNull(afterPreLeave, "afterPreLeave == null");
		this.info.getPlugin().getServer().getPluginManager().registerEvents(
				new PlayerQuitListener(this), this.info.getPlugin());
	}

	@Override
	public void kickAll() {
		this.playerStates.values().stream().map(PlayerInfo::getOfflinePlayer)
				.collect(Collectors.toList()).forEach(this::removePlayer);
	}

	private void updateState(OfflinePlayer player, boolean fullyJoined) {
		DefaultPlayerInfo inf = playerStates.get(player.getUniqueId());
		if (inf == null) {
			inf = new DefaultPlayerInfo();
			playerStates.put(player.getUniqueId(), inf);
		}
		inf.player = player;
		inf.fullyJoined = fullyJoined;
	}

	private void removeState(OfflinePlayer player) {
		playerStates.remove(player.getUniqueId());
	}

	private boolean hasState(OfflinePlayer player) {
		return playerStates.containsKey(player.getUniqueId());
	}

	private boolean hasOnlineState(OfflinePlayer player) {
		DefaultPlayerInfo inf = playerStates.get(player.getUniqueId());
		if (inf == null) {
			return false;
		}
		return inf.isFullyJoined();
	}

	@Override
	public boolean addPlayer(Player player) {
		if (!hasState(player)) {
			if (beforePreJoin.test(player)) {
				updateState(player, false);
				afterPreJoin.accept(player);
			} else {
				return false;
			}
		}
		if (!beforeJoin.test(player)) {
			removePlayer(player);
			return false;
		}
		PlayerJoinMessage join = new PlayerJoinMessage(player);
		trigger.triggerPlayerJoin(join);
		boolean succesful = !join.isCancelled();
		if (succesful) {
			updateState(player, true);
			afterJoin.accept(player);
		} else {
			removePlayer(player);
		}
		return succesful;
	}

	@Override
	public Map<OfflinePlayer, PlayerInfo> getAllPlayers() {
		return playerStates.entrySet().stream().collect(
				Collectors.toMap(i -> info.getPlugin().getServer()
						.getOfflinePlayer(i.getKey()),
						i -> i.getValue()));
	}

	@Override
	public Optional<PlayerInfo> getPlayer(OfflinePlayer player) {
		Objects.requireNonNull(player, "player == null");
		return Optional.ofNullable(this.playerStates.get(player.getUniqueId()));
	}

	@Override
	public Map<Player, PlayerInfo> getPlayers() {
		return playerStates.entrySet().stream().filter(i -> i.getValue().isFullyJoined())
				.collect(Collectors.toMap(i -> info.getPlugin().getServer()
						.getPlayer(i.getKey()),
						i -> i.getValue()));
	}

	@Override
	public void removePlayer(OfflinePlayer player, Reason reason) {
		Objects.requireNonNull(player, "player == null");
		Objects.requireNonNull(reason, "reason == null");
		if (hasState(player)) {
			if (hasOnlineState(player)) {
				Player p;
				if (player instanceof Player) {
					p = (Player) player;
				} else {
					p = getPlayer(player).get().getPlayer();
				}
				trigger.triggerPlayerLeave(new PlayerLeaveMessage(p, reason));
				updateState(player, false);
				afterLeave.accept(p);
			}
			trigger.triggerPlayerPreLeave(new PlayerPreLeaveMessage(player, reason));
			removeState(player);
			afterPreLeave.accept(player);
		}
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
				if (this.hasState(p)) {
					continue;
				}
				if (!this.beforePreJoin.test(p)) {
					failed = true;
					break;
				}
				PlayerPreJoinMessage pl = new PlayerPreJoinMessage(p);
				this.trigger.triggerPlayerPreJoin(pl);
				// This method will automatic call the listeners
				if (pl.isCancelled()) {
					failed = true;
					break;
				}
				updateState(p, false);
				afterPreJoin.accept(p);
				seen++;
			}
		} finally {
			if (failed || seen != total) {
				players.forEach(this::removePlayer);
				failed = true;
			}
		}
		return !failed;
	}

	private static class PlayerQuitListener implements Listener {

		private final Reference<DefaultGameController> ref;

		public PlayerQuitListener(DefaultGameController ref) {
			this.ref = new WeakReference<>(ref);
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
		private OfflinePlayer player;
		private boolean fullyJoined;

		@Override
		public OfflinePlayer getOfflinePlayer() {
			return player;
		}

		@Override
		public Player getPlayer() {
			if (player instanceof Player) {
				return (Player) player;
			} else {
				throw new IllegalStateException("Player not fully joined");
			}
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
			return fullyJoined;
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
