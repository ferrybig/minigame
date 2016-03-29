package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.Future;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.Controller.PlayerInfo;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.impl.StatusPhase;
import me.ferrybig.javacoding.minecraft.minigame.phase.impl.StatusPhase.State;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign.SignType;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSignManager;
import me.ferrybig.javacoding.minecraft.minigame.translation.BaseTranslation;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class DefaultStatusSignManager implements StatusSignManager {

	private final static AtomicLong MODIFICATION_COUNT = new AtomicLong();
	private final Map<Block, StatusSign> signs = new HashMap<>();
	private final Map<Block, Long> modCount = new HashMap<>();
	private final Map<Block, AreaContext> areas = new HashMap<>();
	private final GameCore core;
	private final Translator map;
	private final Listener signListener = new SignListener();
	private boolean stopped = false;

	public DefaultStatusSignManager(GameCore core) {
		this.core = core;
		this.map = core.getInfo().getTranslations();
		this.core.terminationFuture().addListener(f -> {
			assert this.core.terminationFuture() == f;
			stop();
		});
		this.signs.putAll(core.getLoadedSigns());
		this.signs.keySet().forEach(l
				-> modCount.put(l, MODIFICATION_COUNT.incrementAndGet())
		);
		this.core.startingFuture().addListener(f -> {
			assert f == this.core.startingFuture();
			this.signs.forEach(this::updateSign);
			this.core.getInfo().getPlugin().getServer().getPluginManager()
					.registerEvents(signListener, this.core.getInfo().getPlugin());
		});
	}

	private void stop() {
		stopped = true;
	}

	@Override
	public void defineStatusSign(Block location, SignType type) {
		Objects.requireNonNull(type, "type == null");
		Objects.requireNonNull(location, "location == null");
		StatusSign sign = new StatusSignImpl(type);
		core.getInfo().getConfig().saveSign(location, sign);
		signs.put(location, sign);
		modCount.put(location, MODIFICATION_COUNT.incrementAndGet());
		updateSign(location, sign);
	}

	@Override
	public void removeStatusSign(Block location) {
		signs.remove(location);
		modCount.remove(location);
	}

	private void updateSign(Block block, StatusSign sign) {
		if (stopped) {
			return;
		}
		long mod = modCount.getOrDefault(block, 0l);
		BlockState state = block.getState();
		if (!(state instanceof Sign)) {
			removeStatusSign(block);
			return;
		}
		Sign s = (Sign) state;
		s.setLine(0, map.translate(BaseTranslation.SIGNS_HEADER, ""));
		s.setLine(1, map.translate(BaseTranslation.SIGNS_STATE_LOADING, ""));
		s.setLine(2, "");
		s.setLine(3, map.translate(BaseTranslation.SIGNS_FOOTER));
		s.update();

		Future<AreaContext> future = this.core.createRandomGameContext();
		future.addListener(f -> {
			assert f == future;
			if (!f.isSuccess() || future.getNow() == null) {
				if (!f.isCancelled()) {
					core.getInfo().getLogger().log(Level.WARNING,
							"Error while preparing area:", f.cause());
				}
				s.setLine(1, map.translate(BaseTranslation.SIGNS_STATE_ERROR, ""));
				s.update();
				return;
			}
			AreaContext c = future.get();
			long mod2 = modCount.getOrDefault(block, 0l);
			if (mod2 != mod) {
				c.pipeline().terminate();
				return;
			}
			this.areas.put(block, c);
			s.setLine(0, map.translate(BaseTranslation.SIGNS_HEADER, c.getName()));
			updateSignPlayerCount(s, c);
			s.update();
			StatusPhase.registerForStateUpdates(c, (area, areaState) -> {
				assert area == c;
				s.setLine(1, map.translate(areaState));
				s.update();
			}, true);
			c.getController().addListener(new ControllerUpdateListener(() -> {
				updateSignPlayerCount(s, c);
				s.update();
			}));
			c.getClosureFuture().addListener((f1) -> {
				assert f1 == c.getClosureFuture();
				long mod3 = modCount.getOrDefault(block, 0l);
				if (mod3 == mod) {
					this.areas.remove(block);
					updateSign(block, sign);
				}
			});

		});
	}

	private void updateSignPlayerCount(Sign sign, AreaContext context) {
		int nonSpectator = 0;
		int ingame = 0;
		for (PlayerInfo i : context.getController().getPlayers().values()) {
			ingame++;
			if (!i.isSpectator()) {
				nonSpectator++;
			}
		}
		sign.setLine(2, map.translate(BaseTranslation.SIGNS_PLAYERCOUNTER,
				nonSpectator, ingame, context.maxPlayers()));

	}

	private static class StatusSignImpl implements StatusSign {

		private final SignType type;

		public StatusSignImpl(SignType type) {
			this.type = type;
		}

		@Override
		public SignType getType() {
			return type;
		}
	}

	private static class ControllerUpdateListener implements Controller.ControllerListener {

		private final Runnable onPlayerUpdate;

		public ControllerUpdateListener(Runnable onPlayerUpdate) {
			this.onPlayerUpdate = onPlayerUpdate;
		}

		@Override
		public void addedPlayerPreToGame(OfflinePlayer player) {
		}

		@Override
		public void addedPlayerToGame(Player player) {
			onPlayerUpdate.run();
		}

		@Override
		public boolean canAddPlayerPreToGame(OfflinePlayer player) {
			return true;
		}

		@Override
		public boolean canAddPlayerToGame(Player player) {
			return true;
		}

		@Override
		public void removedPlayerFromGame(Player player) {
			onPlayerUpdate.run();
		}

		@Override
		public void removedPlayerFromPreGame(OfflinePlayer player) {
		}
	}

	private class SignListener implements Listener {

		@EventHandler
		public void onInteract(PlayerInteractEvent evt) {
			if (evt.getAction() == Action.RIGHT_CLICK_BLOCK) {
				AreaContext area = areas.get(evt.getClickedBlock());
				if (area == null) {
					return;
				}
				State state = StatusPhase.getState(area);
				if (state != StatusPhase.State.JOINABLE) {
					evt.getPlayer().sendMessage(core.getInfo().getTranslations()
							.translate(BaseTranslation.SIGNS_INTERACT_STARTED));
					return;
				}
				boolean tryJoin = area.getController().addPlayer(evt.getPlayer());
				if (!tryJoin) {
					area.getController().removePlayer(evt.getPlayer());
					evt.getPlayer().sendMessage(core.getInfo().getTranslations()
							.translate(BaseTranslation.SIGNS_INTERACT_FULL));
				} else {
					evt.getPlayer().sendMessage(core.getInfo().getTranslations()
							.translate(BaseTranslation.SIGNS_INTERACT_JOIN, area.getName()));
				}
			}
		}
	}

}
