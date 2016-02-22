/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;

public class DefaultGameRunner {
	private final AreaContext context;
	private final DefaultGamePipeline pipeline;
	private final EventExecutor executor;
	private final Promise<AreaContext> closePromise;
	

	public DefaultGameRunner(AreaContext context, DefaultGamePipeline pipeline, EventExecutor executor) {
		this.context = context;
		this.pipeline = pipeline;
		this.executor = executor;
		this.closePromise = executor.newPromise();
	}
	
	public Future<AreaContext> getCloseFuture() {
		return closePromise;
	}
	
}
