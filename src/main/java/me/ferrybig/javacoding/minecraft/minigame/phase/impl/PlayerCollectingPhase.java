package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import io.netty.util.concurrent.Future;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;
import me.ferrybig.javacoding.minecraft.minigame.translation.BaseTranslation;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;

public class PlayerCollectingPhase extends DefaultPhase {

	private final Set<UUID> onlinePlayers = new HashSet<>();
	private final Set<UUID> offlinePlayers = new HashSet<>();
	private int maxPlayers;
	private int startingPlayerCount;
	private int miniumPlayerCount;
	private final int maxiumWaitTime;
	private boolean acceptingPlayers = true;
	private Future<?> checkLoop;
	private PhaseContext area;
	private int tickCount;

	public PlayerCollectingPhase(int startingPlayerCount, int miniumPlayerCount, int maxiumWaitTime) {
		this.startingPlayerCount = startingPlayerCount;
		this.miniumPlayerCount = miniumPlayerCount;
		this.maxiumWaitTime = maxiumWaitTime;
		Validate.isTrue(miniumPlayerCount >= 0, "miniumPlayerCount must be 0 or higher");
		Validate.isTrue(startingPlayerCount >= miniumPlayerCount,
				"startingPlayerCount must be `miniumPlayerCount` or higher");

	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		this.maxPlayers = Math.max(area.getAreaContext().maxPlayers(), startingPlayerCount);
		this.startingPlayerCount = Math.min(maxPlayers, startingPlayerCount);
		this.miniumPlayerCount = Math.min(startingPlayerCount, miniumPlayerCount);
		assert 0 <= miniumPlayerCount;
		assert miniumPlayerCount <= startingPlayerCount;
		assert startingPlayerCount <= maxPlayers;
		this.area = area;
		checkTick();
	}

	@Override
	public void onPhaseLoad(PhaseContext area) throws Exception {
		offlinePlayers.clear();
		onlinePlayers.clear();
		area.getAreaContext().getController().getPlayers().keySet().stream()
				.map(OfflinePlayer::getUniqueId).peek(offlinePlayers::add)
				.forEach(onlinePlayers::add);
		area.getAreaContext().getController().getAllPlayers().keySet().stream()
				.map(OfflinePlayer::getUniqueId).forEach(offlinePlayers::add);
	}

	@Override
	public void onPhaseUnload(PhaseContext area) throws Exception {
		acceptingPlayers = false;
	}

	@Override
	public void onPlayerLeave(PhaseContext area, PlayerLeaveMessage player) throws Exception {
		super.onPlayerLeave(area, player);
		onlinePlayers.remove(player.getPlayer().getUniqueId());
		player.addListener(this::checkTick);
	}

	@Override
	public void onPlayerJoin(PhaseContext area, PlayerJoinMessage player) throws Exception {
		if (onlinePlayers.size() >= maxPlayers) {
			player.setCancelled(true);
			return;
		}
		player.setCancelled(false);
		super.onPlayerJoin(area, player);
		onlinePlayers.add(player.getPlayer().getUniqueId());
		player.addListener(this::checkTick);
	}

	@Override
	public void onPlayerPreLeave(PhaseContext area, PlayerPreLeaveMessage player) throws Exception {
		super.onPlayerPreLeave(area, player);
		offlinePlayers.remove(player.getPlayer().getUniqueId());
		player.addListener(this::checkTick);
	}

	@Override
	public void onPlayerPreJoin(PhaseContext area, PlayerPreJoinMessage player) throws Exception {
		if (offlinePlayers.size() >= maxPlayers) {
			player.setCancelled(true);
			return;
		}
		player.setCancelled(false);
		super.onPlayerPreJoin(area, player);
		offlinePlayers.add(player.getPlayer().getUniqueId());
		player.addListener(this::checkTick);
	}

	private void taskTick() {
		tickCount--;
		switch (tickCount) {
			case 120:
			case 60:
			case 30:
			case 10:
			case 5:
			case 4:
			case 3:
			case 2:
			case 1:
				this.area.getAreaContext().sendBroadcast(
						BaseTranslation.COUNTDOWN_SECONDS_LEFT, tickCount);
				break;
			default:
		}
		if (tickCount < 1) {
			start();
		} else {
			this.checkLoop = area.getExecutor().schedule(this::taskTick, 1, TimeUnit.SECONDS);
		}
	}

	private void start() {
		if (!acceptingPlayers) {
			return;
		}
		acceptingPlayers = false;
		if (checkLoop != null) {
			checkLoop.cancel(true);
			checkLoop = null;
		}
		this.area.getAreaContext().sendBroadcast(BaseTranslation.COUNTDOWN_STARTED);
		this.area.triggerNextPhase();
	}

	private void checkTick() {
		if (!acceptingPlayers) {
			return;
		}
		int seenPlayers = this.offlinePlayers.size();
		int onlineseenPlayers = this.onlinePlayers.size();
		if (seenPlayers < miniumPlayerCount) {
			if (checkLoop != null) {
				this.area.getAreaContext().sendBroadcast(BaseTranslation.COUNTDOWN_CANCELLED);
				checkLoop.cancel(true);
				checkLoop = null;
			}
		} else if (seenPlayers < startingPlayerCount) {
			// Do nothing
		} else if (onlineseenPlayers < maxPlayers) {
			if (checkLoop == null) {
				this.area.getAreaContext().sendBroadcast(BaseTranslation.COUNTDOWN_STARTING);
				this.tickCount = maxiumWaitTime;
				this.checkLoop = area.getExecutor().schedule(this::taskTick, 1, TimeUnit.SECONDS);
			}
		} else {
			// All limits reached
			start();
		}
	}

}
