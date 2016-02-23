/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame.phase;

import io.netty.util.concurrent.Future;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.ferrybig.javacoding.minecraft.minigame.PhaseContext;
import org.apache.commons.lang.Validate;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlayerCollectingPhase extends DefaultPhase{
	
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
		area.getAreaContext().getController().getTotalPlayers().stream()
				.map(OfflinePlayer::getUniqueId).peek(offlinePlayers::add)
				.forEach(onlinePlayers::add);
		area.getAreaContext().getController().getPendingPlayers().stream()
				.map(OfflinePlayer::getUniqueId).forEach(offlinePlayers::add);
	}
	
	@Override
	public void onPhaseUnload(PhaseContext area) throws Exception {
		acceptingPlayers = false;
	}

	@Override
	public void onPlayerLeave(PhaseContext area, Player player) throws Exception {
		onlinePlayers.remove(player.getUniqueId());
	}

	@Override
	public boolean onPlayerJoin(PhaseContext area, Player player) throws Exception {
		if(onlinePlayers.size() >= maxPlayers)
			return false;
		onlinePlayers.add(player.getUniqueId());
		return true;
	}

	@Override
	public void onPlayerPreLeave(PhaseContext area, OfflinePlayer player) throws Exception {
		offlinePlayers.remove(player.getUniqueId());
	}

	@Override
	public boolean onPlayerPreJoin(PhaseContext area, OfflinePlayer player) throws Exception {
		if(offlinePlayers.size() >= maxPlayers)
			return false;
		offlinePlayers.add(player.getUniqueId());
		return true;
	}
	
	private void checkTick(PhaseContext area) {
		
	}
	
}
