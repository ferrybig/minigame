
package me.ferrybig.javacoding.minecraft.minigame.information;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class AreaInformationBuilder {

	private String name;
	private boolean enabled = true;
	private String description = "";
	private Map<String, List<Block>> taggedBlocks = Collections.emptyMap();
	private Map<String, List<Location>> taggedLocations = Collections.emptyMap();
	private Selection bounds;
	private int maxPlayers = Byte.MAX_VALUE;
	
	public AreaInformationBuilder() {
	}
	
	public AreaInformationBuilder(Selection bounds) {
		this.bounds = bounds;
	}

	public AreaInformationBuilder(String name) {
		this.name = name;
	}
	
	public AreaInformationBuilder(String name, Selection bounds) {
		this.name = name;
		this.bounds = bounds;
	}
	
	public AreaInformationBuilder(AreaInformation other) {
		this.name = other.getName();
		this.enabled = other.isEnabled();
		this.description = other.getDescription();
		this.bounds = other.getBounds().deepClone();
		this.maxPlayers = other.maxPlayers();
		this.taggedBlocks = new HashMap<>(other.getTaggedBlocks());
		this.taggedBlocks.replaceAll((k,v)->new ArrayList<>(v));
		this.taggedLocations = new HashMap<>(other.getTaggedLocations());
		this.taggedLocations.replaceAll((k,v)->new ArrayList<>(v));
	}

	public AreaInformationBuilder setName(String name) {
		this.name = name;
		return this;
	}

	public AreaInformationBuilder setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public AreaInformationBuilder setDescription(String description) {
		this.description = description;
		return this;
	}

	public AreaInformationBuilder setTaggedBlocks(Map<String, java.util.List<org.bukkit.block.Block>> taggedBlocks) {
		this.taggedBlocks = taggedBlocks;
		return this;
	}

	public AreaInformationBuilder setTaggedLocations(Map<String, java.util.List<org.bukkit.Location>> taggedLocations) {
		this.taggedLocations = taggedLocations;
		return this;
	}
	
	public AreaInformationBuilder setTaggedBlocksUneditable() {
		this.taggedBlocks = new HashMap<>(taggedBlocks);
		this.taggedBlocks.replaceAll((k,v)->Collections.unmodifiableList(new ArrayList<>(v)));
		this.taggedBlocks = Collections.unmodifiableMap(taggedBlocks);
		return this;
	}
	
	public AreaInformationBuilder setTaggedLocationsUneditable() {
		this.taggedLocations = new HashMap<>(taggedLocations);
		this.taggedLocations.replaceAll((k,v)->Collections.unmodifiableList(new ArrayList<>(v)));
		this.taggedLocations = Collections.unmodifiableMap(taggedLocations);
		return this;
	}
	
	public AreaInformationBuilder setUneditable() {
		setTaggedLocationsUneditable();
		setTaggedBlocksUneditable();
		return this;
	}

	public AreaInformationBuilder setBounds(Selection bounds) {
		this.bounds = bounds;
		return this;
	}

	public AreaInformationBuilder setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
		return this;
	}

	public AreaInformation create() {
		if(bounds == null) {
			throw new IllegalStateException("bounds not defined");
		}
		if(name == null) {
			throw new IllegalStateException("name not defined");
		}
		return new DefaultAreaInformation(name, enabled, description, taggedBlocks, taggedLocations, bounds, maxPlayers);
	}
	
	public static AreaInformation copyOf(AreaInformation other) {
		return new AreaInformationBuilder(other).create();
	}
	
	public static AreaInformationBuilder from(AreaInformation other) {
		return new AreaInformationBuilder(other);
	}

}
