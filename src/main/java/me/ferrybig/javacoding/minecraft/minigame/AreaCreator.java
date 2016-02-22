/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import java.util.List;
import java.util.Map;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.core.DefaultAreaCreator;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface AreaCreator {

	public DefaultAreaCreator setDescription(String description);

	public DefaultAreaCreator setMaxPlayers(int maxPlayers);

	public DefaultAreaCreator setName(String name);

	public DefaultAreaCreator setSelection(Selection selection);

	public DefaultAreaCreator setTaggedBlocks(Map<Object, List<Block>> taggedBlocks);

	public DefaultAreaCreator setTaggedLocations(Map<Object, List<Location>> taggedLocations);

	public DefaultAreaCreator setValidTeams(Set<Object> validTeams);

	public Area createArea();
	
}
