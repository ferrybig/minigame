package me.ferrybig.javacoding.minecraft.minigame;

import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

public interface PlayerController {
	public void setPlayerTeam(Player player, Object team);
	
	public Object getPlayerTeam(Player player);
	
	public Map<Player, Object> getAllPlayerTeams();
	
	public default <T> T getPlayerTeam(Player player, Class<T> teamObject) {
		Object t = getPlayerTeam(player);
		if(teamObject.isInstance(t)) {
			return teamObject.cast(t);
		}
		throw new ClassCastException(t + " not instance of " + teamObject);
	}
	
	public void setSpectator(Player player, boolean spectator);
	
	public boolean isSpectator(Player player);
	
	public default Collection<Player> getNonSpectators() {
		return getNonSpectatorsAsStream().collect(Collectors.toList());
	}
	
	public default Stream<Player> getNonSpectatorsAsStream() {
		return getPlayersAsStream().filter(i->!isSpectator(i));
	}
	
	public default Collection<Player> getSpectators() {
		return getSpectatorsAsStream().collect(Collectors.toList());
	}
	
	public default Stream<Player> getSpectatorsAsStream() {
		return getPlayersAsStream().filter(this::isSpectator);
	}
	
	public Collection<Player> getPlayers();
	
	public default Stream<Player> getPlayersAsStream() {
		return getPlayers().stream();
	}
}
