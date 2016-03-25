package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.util.Objects;

public abstract class AbstractConfig implements Config {

	protected final EventExecutor executor;

	public AbstractConfig(EventExecutor executor) {
		this.executor = Objects.requireNonNull(executor);
	}

	@Override
	public Future<?> flushChanges() {
		return executor.newSucceededFuture(null);
	}

	@Override
	public void close() {
	}

}
