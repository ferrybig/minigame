package me.ferrybig.javacoding.minecraft.minigame;

import java.util.Collection;
import java.util.List;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public interface Controller {
	
	public boolean tryAddPlayer(List<? extends OfflinePlayer> player);
	
	public boolean addPlayer(Player player);
	
	public void removePlayer(OfflinePlayer player);
	
	public int getGameSize();
	
	public int getSlotsLeft();
	
	public Collection<? extends OfflinePlayer> getPendingPlayers();
			
}
