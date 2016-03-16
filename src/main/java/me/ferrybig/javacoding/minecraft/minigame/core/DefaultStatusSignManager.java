
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
import me.ferrybig.javacoding.minecraft.minigame.phase.StatusPhase;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign.SignType;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSignManager;
import me.ferrybig.javacoding.minecraft.minigame.translation.BaseTranslation;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class DefaultStatusSignManager implements StatusSignManager {

	private final static AtomicLong MODIFICATION_COUNT = new AtomicLong();
	private final Map<Block, StatusSign> signs = new HashMap<>();
	private final Map<Block, Long> modCount = new HashMap<>();
	private final GameCore core;
	private final TranslationMap map;
	private boolean stopped = false;

	public DefaultStatusSignManager(GameCore core, Map<Block, StatusSign> signs) {
		this.core = core;
		this.map = core.getInfo().getTranslations();
		this.core.terminationFuture().addListener(f->{
			stop();
		});
		this.signs.putAll(signs);
		this.signs.keySet().forEach(l->
			modCount.put(l, MODIFICATION_COUNT.incrementAndGet())
		);
		this.core.startingFuture().addListener(f->{
			this.signs.forEach(this::updateSign);
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
		signs.put(location,sign);
		modCount.put(location, MODIFICATION_COUNT.incrementAndGet());
		updateSign(location, sign);
	}

	@Override
	public void removeStatusSign(Block location) {
		signs.remove(location);
		modCount.remove(location);
	}

	private void updateSign(Block block, StatusSign sign) {

		long mod = modCount.getOrDefault(block, 0l);
		BlockState state = block.getState();
		if(!(state instanceof Sign)) {
			removeStatusSign(block);
			return;
		}
		Sign s = (Sign)state;
		s.setLine(0, map.get(BaseTranslation.SIGNS_HEADER, ""));
		s.setLine(1, map.get(BaseTranslation.SIGNS_STATE_LOADING, ""));
		s.setLine(2, "");
		s.setLine(3, map.get(BaseTranslation.SIGNS_FOOTER));
		s.update();

		Future<AreaContext> future = this.core.createRandomGameContext();
		future.addListener(f->{
			assert f == future;
			if(!f.isSuccess() || future.getNow() == null) {
				if(!f.isCancelled()) {
					core.getInfo().getLogger().log(Level.WARNING,
							"Error while preparing area:", f.cause());
				}
				s.setLine(1, map.get(BaseTranslation.SIGNS_STATE_ERROR, ""));
				s.update();
				return;
			}
			AreaContext c = future.get();
			s.setLine(0, map.get(BaseTranslation.SIGNS_HEADER, c.getName()));
			updateSignPlayerCount(s, c);
			s.update();
			StatusPhase.registerForStateUpdates(c, (area, areaState)->{
				assert area == c;
				s.setLine(1, map.get(areaState));
				s.update();
			}, true);
			c.getController().addListener(new ControllerUpdateListener(()->{
				updateSignPlayerCount(s, c);
				s.update();
			}));
			c.getClosureFuture().addListener((f1)->{
				assert f1 == c.getClosureFuture();
				long mod2 = modCount.getOrDefault(block, 0l);
				if(mod2 == mod)
					updateSign(block, sign);
			});

		});
	}

	private void updateSignPlayerCount(Sign sign, AreaContext context) {
		int nonSpectator = 0;
		int ingame = 0;
		for(PlayerInfo i : context.getController().getPlayers().values()) {
			ingame++;
			if (!i.isSpectator()) {
				nonSpectator++;
			}
		}
		sign.setLine(2, map.get(BaseTranslation.SIGNS_PLAYERCOUNTER,
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

}
