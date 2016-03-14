
package me.ferrybig.javacoding.minecraft.minigame.information;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class DefaultAreaInformation implements AreaInformation {
	
	private final String description;
	private final String name;
	private final boolean enabled;
	private final Map<String, List<Block>> taggedBlocks;
	private final Map<String, List<Location>> taggedLocations;
	private final Selection bounds;
	private final int maxPlayers;
	
	public DefaultAreaInformation(String name, boolean enabled, String description, 
			Map<String, List<Block>> taggedBlocks, 
			Map<String, List<Location>> taggedLocations, Selection bounds, int maxPlayers) {
		this.description = Objects.requireNonNull(description);
		this.name = Objects.requireNonNull(name);
		this.taggedBlocks = Objects.requireNonNull(taggedBlocks);
		this.taggedLocations = Objects.requireNonNull(taggedLocations);
		this.bounds = Objects.requireNonNull(bounds);
		this.maxPlayers = Objects.requireNonNull(maxPlayers);
		this.enabled = enabled;
	}

	@Override
	public Selection getBounds() {
		return bounds;
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
	public int maxPlayers() {
		return maxPlayers;
	}
	
	public static AreaInformationBuilder builder(String name) {
		return new AreaInformationBuilder(name);
	}

}
