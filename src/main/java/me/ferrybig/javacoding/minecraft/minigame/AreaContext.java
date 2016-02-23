/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.AttributeMap;
import java.util.concurrent.ExecutorService;
import org.bukkit.plugin.Plugin;

public interface AreaContext extends AttributeMap, AreaInformation {
	public Area getArea();
	
	public Pipeline pipeline();
	
	public ExecutorService getExecutor();
	
	public long instanceId();
	
	public Controller getController();
	
	public Plugin getPlugin();
}
