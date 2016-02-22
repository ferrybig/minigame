/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
