package me.ferrybig.javacoding.minecraft.minigame.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class DefaultAreaCreator implements AreaCreator {

	private final AreaVerifier verifier;
	private final Function<? super AreaCreator, Area> constructor;
	private String name = "Unnamed";
	private String description = "";
	private Map<String, List<Block>> taggedBlocks = new HashMap<>();
	private Map<String, List<Location>> taggedLocations = new HashMap<>();
	private Selection selection;
	private boolean enabled;
	private int maxPlayers = 0;

	public DefaultAreaCreator(Function<? super AreaCreator, Area> constructor,
			AreaVerifier verifier) {
		this.constructor = Objects.requireNonNull(constructor, "constructor == null");
		this.verifier = Objects.requireNonNull(verifier, "verifier == null");
	}

	public DefaultAreaCreator(World w, Function<? super AreaCreator, Area> constructor,
			AreaVerifier verifier) {
		this(constructor, verifier);
		this.selection = new DefaultSelection(w);
	}

	@Override
	public Area createArea() {
		return constructor.apply(this);
	}

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
	public boolean isValid() {
		return verifier.isCorrect(this);
	}

	@Override
	public int maxPlayers() {
		return maxPlayers;
	}

	@Override
	public DefaultAreaCreator setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public DefaultAreaCreator setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public DefaultAreaCreator setTaggedBlocks(Map<String, List<Block>> taggedBlocks) {
		this.taggedBlocks = taggedBlocks;
		return this;
	}

	@Override
	public DefaultAreaCreator setTaggedLocations(Map<String, List<Location>> taggedLocations) {
		this.taggedLocations = taggedLocations;
		return this;
	}

	@Override
	public DefaultAreaCreator setSelection(Selection selection) {
		this.selection = selection;
		return this;
	}

	@Override
	public DefaultAreaCreator setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
		return this;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public DefaultAreaCreator setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	@Override
	public Set<String> validTeams() {
		return verifier.getValidTeams(this);
	}
}
