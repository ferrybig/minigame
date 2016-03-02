package me.ferrybig.javacoding.minecraft.minigame.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import me.ferrybig.javacoding.minecraft.minigame.DefaultSelection;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.CachedAreaVerifier;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class DefaultAreaCreator<A extends Area> implements AreaCreator {

	private final CachedAreaVerifier area;
    private String name = "Unnamed";
	private String description = "";
	private Set<String> validTeams = new HashSet<>();
	private Map<String, List<Block>> taggedBlocks = new HashMap<>();
	private Map<String, List<Location>> taggedLocations = new HashMap<>();
	private Selection selection;
	private boolean enabled;
	private int maxPlayers = 0;

	public DefaultAreaCreator(World w, CachedAreaVerifier area) {
		this.selection = new DefaultSelection(w);
		this.area = area;
	}

	@Override
	public A createArea() {
		throw new UnsupportedOperationException(); //TODO
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
		return area.verifyInformation(this, translations);
	}

	@Override
	public int maxPlayers() {
		return maxPlayers;
	}

	@Override
	public DefaultAreaCreator<A> setName(String name) {
		this.name = name;
		return this;
	}

	@Override
	public DefaultAreaCreator<A> setDescription(String description) {
		this.description = description;
		return this;
	}

	@Override
	public DefaultAreaCreator<A> setValidTeams(Set<String> validTeams) {
		this.validTeams = validTeams;
		return this;
	}

	@Override
	public DefaultAreaCreator<A> setTaggedBlocks(Map<String, List<Block>> taggedBlocks) {
		this.taggedBlocks = taggedBlocks;
		return this;
	}

	@Override
	public DefaultAreaCreator<A> setTaggedLocations(Map<String, List<Location>> taggedLocations) {
		this.taggedLocations = taggedLocations;
		return this;
	}

	@Override
	public DefaultAreaCreator<A> setSelection(Selection selection) {
		this.selection = selection;
		return this;
	}

	@Override
	public DefaultAreaCreator<A> setMaxPlayers(int maxPlayers) {
		this.maxPlayers = maxPlayers;
		return this;
	}

	@Override
	public Set<String> validTeams() {
		return validTeams;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public DefaultAreaCreator<A> setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}
}
