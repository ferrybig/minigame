package me.ferrybig.javacoding.minecraft.minigame.phase;

import io.netty.util.concurrent.Future;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerCollectingPhase extends DefaultPlayerPhase{
	
	private final Set<UUID> onlinePlayers = new HashSet<>();
	
	private final Set<UUID> offlinePlayers = new HashSet<>();
	
	private int playersSeen;
	
	private int maxPlayers;
	
	private int startingPlayerCount;
	
	private int miniumPlayerCount;
	
	private final int maxiumWaitTime;
	
	private boolean acceptingPlayers = true;
	
	private Future<?> checkLoop;

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
	}
	
	@Override
	public void onPhaseLoad(PhaseContext area) throws Exception {
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
	}

	@Override
	public void onPlayerJoin(PhaseContext area, PlayerJoinMessage player) throws Exception {
		if(onlinePlayers.size() >= maxPlayers) {
			player.setCancelled(true);
			return;
		}
		super.onPlayerJoin(area, player);
		onlinePlayers.add(player.getPlayer().getUniqueId());
	}

	@Override
	public void onPlayerPreLeave(PhaseContext area, PlayerPreLeaveMessage player) throws Exception {
		super.onPlayerPreLeave(area, player);
		offlinePlayers.remove(player.getPlayer().getUniqueId());
	}

	@Override
	public void onPlayerPreJoin(PhaseContext area, PlayerPreJoinMessage player) throws Exception {
		if(offlinePlayers.size() >= maxPlayers) {
			player.setCancelled(true);
			return;
		}
		super.onPlayerPreJoin(area, player);
		offlinePlayers.add(player.getPlayer().getUniqueId());
	}
	
	private void checkTick(PhaseContext area) {
		
	}
	
}
