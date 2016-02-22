/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.AttributeMap;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface Area extends AttributeMap {

	public String getName();

	public Selection getBounds();

	public Collection<Block> getTaggedBlocks(Object tag);

	public Collection<Location> getTaggedLocations(Object tag);

	public Set<Object> validTeams();

	public int maxPlayers();

	public AreaContext newInstance();

	public default AreaContext newInstance(Consumer<AreaContext> decorator) {
		AreaContext inst = newInstance();
		decorator.accept(inst);
		return inst;
	}

	public AreaCreator editArea();
}
