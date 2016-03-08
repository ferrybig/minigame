package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.AttributeMap;
import io.netty.util.concurrent.Future;
import java.util.concurrent.ExecutorService;

public interface AreaContext extends AttributeMap, ResolvedAreaInformation {
	public Area getArea();
	
	public Pipeline pipeline();
	
	public ExecutorService getExecutor();
	
	public long instanceId();
	
	public Controller getController();
	
	public GameCore getCore();
	
	public default InformationContext getInformationContext() {
		return getCore().getInfo();
	}
	
	public default Future<?> getClosureFuture() {
		return pipeline().getClosureFuture();
	}
}
