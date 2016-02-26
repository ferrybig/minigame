
package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;

public interface Config extends AutoCloseable {
	public Future<?> flushChanges();
	
	@Override
	public void close();
}
