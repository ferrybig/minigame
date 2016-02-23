/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.minecraft.minigame;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface AreaInformation {

	public String getName();
	
	public String getDescription();

	public Selection getBounds();
	
	public Map<Object, List<Block>> getTaggedBlocks();

	public default List<Block> getTaggedBlocks(Object tag) {
		
		List<Block> get = getTaggedBlocks().get(tag);
		if(get == null)
			return Collections.emptyList();
		return get;
	}
	
	public Map<Object, List<Location>> getTaggedLocations();

	public default List<Location> getTaggedLocations(Object tag) {
		List<Location> get = getTaggedLocations().get(tag);
		if(get == null)
			return Collections.emptyList();
		return get;
	}

	public Set<Object> validTeams();

	public int maxPlayers();
	
	public default AreaInformation getInformationCopy() {
		String name = getName();
		String description = getDescription();
		Selection selection = getBounds().deepClone();
		Set<Object> validTeams = new HashSet<>(validTeams());
		int maxPlayers = maxPlayers();
		Map<Object,List<Location>> taggedLocations = new HashMap<>(getTaggedLocations());
		taggedLocations.replaceAll((k,v)->new ArrayList<>(v));
		Map<Object,List<Block>> taggedBlocks = new HashMap<>(getTaggedBlocks());
		taggedBlocks.replaceAll((k,v)->new ArrayList<>(v));
		return new AreaInformation() {
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
			public Set<Object> validTeams() {
				return validTeams;
			}
		};
	}
}
