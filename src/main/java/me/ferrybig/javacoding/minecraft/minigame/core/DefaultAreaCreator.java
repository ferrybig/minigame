/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame.core;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.Selection;

public class DefaultAreaCreator implements AreaCreator {
    private String name = "Unnamed";
	private String description = "";
	private Set<Object> validTeams = Collections.emptySet();
	private Map<Object, java.util.List<org.bukkit.block.Block>> taggedBlocks = Collections.emptyMap();
	private Map<Object, java.util.List<org.bukkit.Location>> taggedLocations = Collections.emptyMap();
	private Selection selection = null;
	private int maxPlayers = 0;

	public DefaultAreaCreator() {
	}

	@Override
	public Area createArea() {
		throw new UnsupportedOperationException("Not supported yet.");
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
	public DefaultAreaCreator setTaggedBlocks(Map<Object, java.util.List<org.bukkit.block.Block>> taggedBlocks) {
		this.taggedBlocks = taggedBlocks;
		return this;
	}

	@Override
	public DefaultAreaCreator setTaggedLocations(Map<Object, java.util.List<org.bukkit.Location>> taggedLocations) {
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
}
