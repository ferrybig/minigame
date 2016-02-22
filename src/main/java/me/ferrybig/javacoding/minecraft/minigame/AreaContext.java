/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.AttributeMap;
import java.util.Collection;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface AreaContext {
	public Area getArea();
	
	public Pipeline pipeline();
	
	public long instanceId();
	
	public Controller getController();
	
	public Collection<Block> getTaggedBlocks(Object tag);
	
	public Collection<Location> getTaggedLocations(Object tag);
	
	public Set<Object> validTeams();
	
	public int maxPlayers();
}
