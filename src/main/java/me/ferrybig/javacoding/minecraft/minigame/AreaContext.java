/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.AttributeMap;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface AreaContext extends AttributeMap {
	public void triggerPhaseUpdate();
	
	public Area getArea();
	
	public Location getLocation(Loc loc);
	
	public Block getBlock(Loc loc);
	
	public long instanceId();
	
	public void triggerNextPhase();
	
	public void triggerNextPhase(String next);
	
	public void triggerNextPhase(Class<?> next);
}
