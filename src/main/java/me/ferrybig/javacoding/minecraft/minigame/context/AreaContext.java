package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.AttributeMap;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.InformationContext;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.information.ResolvedAreaInformation;

public interface AreaContext extends AttributeMap, ResolvedAreaInformation {
	public Area getArea();
	
	public Pipeline pipeline();
	
	public EventExecutor getExecutor();
	
	public long instanceId();
	
	public Controller getController();
	
	public GameCore getCore();
	
	public default InformationContext getInformationContext() {
		return getCore().getInfo();
	}
	
	public default Future<?> getClosureFuture() {
		return pipeline().getClosureFuture();
	}

	public default Logger getLogger() {
		return getCore().getInfo().getLogger();
	}
}
