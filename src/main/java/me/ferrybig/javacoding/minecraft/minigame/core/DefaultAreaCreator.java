/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.DefaultSelection;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class DefaultAreaCreator<A extends Area> implements AreaCreator {

	private final Function<? super DefaultAreaCreator, A> areaMaker;
    private String name = "Unnamed";
	private String description = "";
	private Set<Object> validTeams = new HashSet<>();
	private Map<Object, List<Block>> taggedBlocks = new HashMap<>();
	private Map<Object, List<Location>> taggedLocations = new HashMap<>();
	private Selection selection;
	private int maxPlayers = 0;

	public DefaultAreaCreator(World w, Function<? super DefaultAreaCreator, A> areaMaker) {
		this.selection = new DefaultSelection(w);
		this.areaMaker = areaMaker;
	}

	@Override
	public A createArea() {
		return areaMaker.apply(this);
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
	public Map<Object, List<Block>> getTaggedBlocks() {
		return taggedBlocks;
	}

	@Override
	public Map<Object, List<Location>> getTaggedLocations() {
		return taggedLocations;
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
	public DefaultAreaCreator setValidTeams(Set<Object> validTeams) {
		this.validTeams = validTeams;
		return this;
	}

	@Override
	public DefaultAreaCreator setTaggedBlocks(Map<Object, List<Block>> taggedBlocks) {
		this.taggedBlocks = taggedBlocks;
		return this;
	}

	@Override
	public DefaultAreaCreator setTaggedLocations(Map<Object, List<Location>> taggedLocations) {
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
	public Set<Object> validTeams() {
		return validTeams;
	}
}
