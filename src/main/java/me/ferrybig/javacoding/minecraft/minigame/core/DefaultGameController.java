package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.DefaultAttributeMap;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
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
import static me.ferrybig.javacoding.minecraft.minigame.util.SafeUtil.safeCall;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class DefaultGameController implements Controller {

	private final InformationContext info;
	private final Triggerable trigger;
	private final List<ControllerListener> listeners = new ArrayList<>();
	private final Map<UUID, DefaultPlayerInfo> playerStates = new HashMap<>();
	private final AtomicBoolean inCheckLoop = new AtomicBoolean(false);

	public DefaultGameController(InformationContext info, Triggerable trigger) {
		this.info = Objects.requireNonNull(info, "info == null");
		this.trigger = Objects.requireNonNull(trigger, "trigger == null");
		this.info.getPlugin().getServer().getPluginManager().registerEvents(
				new PlayerQuitListener(this), this.info.getPlugin());
	}

	@Override
	public void addListener(ControllerListener listener) {
		this.listeners.add(listener);
	}

	private <T> void callListeners(BiConsumer<ControllerListener, T> caller, T argument) {
		listeners.stream().forEachOrdered((l) -> safeCall(info.getLogger(), () -> caller.accept(l, argument)));
	}

	private <T> boolean askListeners(BiFunction<ControllerListener, T, Boolean> caller, T argument) {
		return listeners.stream().allMatch((l) -> safeCall(info.getLogger(), () -> caller.apply(l, argument), false));
	}

	@Override
	public void kickAll() {
		this.playerStates.values().stream().map(PlayerInfo::getOfflinePlayer)
				.collect(Collectors.toList()).forEach(this::removePlayer);
	}

	@Override
	public void removeListener(ControllerListener listener) {
		this.listeners.remove(listener);
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
		Objects.requireNonNull(player, "player == null");
		if(inCheckLoop.get())
			return false;
		inCheckLoop.set(true);
		try {
			if (!hasState(player)) {
				tryAddPlayer(player);
			}
			if (!askListeners(ControllerListener::canAddPlayerToGame, player)) {
				removePlayer(player);
				return false;
			}
			PlayerJoinMessage join = new PlayerJoinMessage(player);
			trigger.triggerPlayerJoin(join);
			boolean succesful = !join.isCancelled();
			if (succesful) {
				updateState(player, true);
				callListeners(ControllerListener::addedPlayerToGame, player);
			} else {
				removePlayer(player);
			}
			return succesful;
		} finally {
			inCheckLoop.set(false);
		}
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
				callListeners(ControllerListener::removedPlayerFromGame, p);
			}
			trigger.triggerPlayerPreLeave(new PlayerPreLeaveMessage(player, reason));
			removeState(player);
			callListeners(ControllerListener::removedPlayerFromPreGame, player);
		}
	}

	public boolean tryAddPlayer(OfflinePlayer player) {
		Objects.requireNonNull(player, "player == null");
		if(inCheckLoop.get())
			return false;
		inCheckLoop.set(true);
		try {
			if (!hasState(player)) {
				if (askListeners(ControllerListener::canAddPlayerPreToGame, player)) {
					PlayerPreJoinMessage pl = new PlayerPreJoinMessage(player);
					this.trigger.triggerPlayerPreJoin(pl);
					if (pl.isCancelled()) {
						return false;
					}
					updateState(player, false);
					callListeners(ControllerListener::addedPlayerPreToGame, player);
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} finally {
			inCheckLoop.set(false);
		}
	}

	@Override
	public boolean tryAddPlayer(List<? extends OfflinePlayer> players) {
		Objects.requireNonNull(players, "players == null");
		if(inCheckLoop.get())
			return false;
		inCheckLoop.set(true);
		try {
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
					if (!tryAddPlayer(p)) {
						failed = true;
						break;
					}
					seen++;
				}
			} finally {
				if (failed || seen != total) {
					players.forEach(this::removePlayer);
					failed = true;
				}
			}
			return !failed;
		} finally {
			inCheckLoop.set(false);
		}
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
