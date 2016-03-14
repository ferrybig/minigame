
package me.ferrybig.javacoding.minecraft.minigame.information;

import java.util.List;
import java.util.Map;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class DefaultResolvedAreaInformation extends DefaultAreaInformation 
		implements ResolvedAreaInformation {

	private final boolean valid;

	private final Set<String> teams;

	public DefaultResolvedAreaInformation(String name, boolean enabled, String description, 
			Map<String, List<Block>> taggedBlocks, Map<String, List<Location>> taggedLocations, 
			Selection bounds, int maxPlayers, boolean valid, Set<String> teams) {
		super(name, enabled, description, taggedBlocks, taggedLocations, bounds, maxPlayers);
		this.valid = valid;
		this.teams = teams;
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public Set<String> validTeams() {
		return teams;
	}

}
