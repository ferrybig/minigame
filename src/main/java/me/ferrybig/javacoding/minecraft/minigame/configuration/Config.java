package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.Future;

/**
 * Default interface for any config related classes
 *
 * @author Fernando
 */
public interface Config extends AutoCloseable {

	/**
	 * Flush changes to this config file to the disk, if required
	 *
	 * @return
	 */
	public Future<?> flushChanges();

	/**
	 * Closes this config. A closed configuration cannot be interacted with.<p>
	 * Configuration providers are free to ignore calls to this method.
	 * <p>
	 * Configuration providers should allow calls to this method even if the
	 * config is closed. These calls must be ignored by the underlying system
	 */
	@Override
	public void close();
}
