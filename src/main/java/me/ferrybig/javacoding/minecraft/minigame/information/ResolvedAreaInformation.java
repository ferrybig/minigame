package me.ferrybig.javacoding.minecraft.minigame.information;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface ResolvedAreaInformation extends AreaInformation {

	public boolean isValid();

	public Set<String> validTeams();

	@Override
	public default boolean canBeUsed() {
		return isEnabled() && isValid();
	}

	@Override
	public default ResolvedAreaInformation getInformationCopy() {
		boolean enabled = isEnabled();
		String name = getName();
		String description = getDescription();
		Selection selection = getBounds().deepClone();
		boolean valid = isValid();
		Set<String> validTeams = new HashSet<>(validTeams());
		int maxPlayers = maxPlayers();
		Map<String, List<Location>> taggedLocations = new HashMap<>(getTaggedLocations());
		taggedLocations.replaceAll((k, v) -> new ArrayList<>(v));
		Map<String, List<Block>> taggedBlocks = new HashMap<>(getTaggedBlocks());
		taggedBlocks.replaceAll((k, v) -> new ArrayList<>(v));
		return new ResolvedAreaInformation() {
			@Override
			public Selection getBounds() {
				return selection;
			}

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public Map<String, List<Block>> getTaggedBlocks() {
				return taggedBlocks;
			}

			@Override
			public Map<String, List<Location>> getTaggedLocations() {
				return taggedLocations;
			}

			@Override
			public boolean isEnabled() {
				return enabled;
			}

			@Override
			public boolean isValid() {
				return valid;
			}

			@Override
			public int maxPlayers() {
				return maxPlayers;
			}

			@Override
			public Set<String> validTeams() {
				return validTeams;
			}
		};
	}
}
