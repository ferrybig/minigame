package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;

public interface GameRunner<T extends GameRunner<T>> {
	public Future<AreaContext> closeFuture();
	
	public Future<T> startFuture();
	
	public void start();
	
	public default boolean isStarted() {
		return startFuture().isDone();
	}
	
	public void terminateGame();
}
