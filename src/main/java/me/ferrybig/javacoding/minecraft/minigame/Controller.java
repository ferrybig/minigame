/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import java.util.Collection;
import java.util.List;
import me.ferrybig.javacoding.minecraft.party.util.Joinable;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Controller {
	
	public boolean tryAddPlayer(List<? extends OfflinePlayer> player);
	
	public boolean addPlayer(Player player);
	
	public int getGameSize();
	
	public int getSlotsLeft();
	
	public boolean allowsNewPlayers();
	
	public void setAllowNewPlayers(boolean allow);
	
	public void removePlayer(OfflinePlayer player);
	
	public void setPlayerTeam(Player player, Object team);
	
	public Object getPlayerTeam(Player player);
	
	public void setSpectator(Player player, boolean spectator);
	
	public boolean isSpectator(Player player);
	
	public Collection<? extends Player> getActivePlayers();
	
	public Collection<? extends Player> getTotalPlayers();
	
	public default Joinable<OfflinePlayer, Player> asJoinable() {
		return new Joinable<OfflinePlayer, Player>() {
			@Override
			public long freeSlotsLeft() {
				return getSlotsLeft();
			}

			@Override
			public boolean joinOfflinePlayer(List<? extends OfflinePlayer> players) {
				return tryAddPlayer(players);
			}

			@Override
			public void joinOnlinePlayer(Player player) {
				addPlayer(player);
			}

			@Override
			public void quitOfflinePlayer(List<? extends OfflinePlayer> players) {
				players.forEach(Controller.this::removePlayer);
			}
		};
	};
			
}
