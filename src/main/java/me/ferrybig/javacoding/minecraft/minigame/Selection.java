/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import org.bukkit.World;

public interface Selection {
	public World getWorld();
	
	public IntegerLoc getFirstPoint();
	
	public IntegerLoc getSecondPoint();
}
