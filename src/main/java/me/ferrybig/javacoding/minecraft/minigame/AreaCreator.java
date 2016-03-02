package me.ferrybig.javacoding.minecraft.minigame;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface AreaCreator extends ResolvedAreaInformation {

	public AreaCreator setDescription(String description);

	public AreaCreator setMaxPlayers(int maxPlayers);

	public AreaCreator setName(String name);

	public AreaCreator setSelection(Selection selection);

	public AreaCreator setTaggedBlocks(Map<String, List<Block>> taggedBlocks);

	public AreaCreator setTaggedLocations(Map<String, List<Location>> taggedLocations);

	public AreaCreator setValidTeams(Set<String> validTeams);

	public Area createArea();
	
}
