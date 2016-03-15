package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.concurrent.EventExecutor;
import me.ferrybig.javacoding.minecraft.minigame.Triggerable;
import org.bukkit.event.Listener;

public interface PhaseContext extends Triggerable {

	public AreaContext getAreaContext();

	public default EventExecutor getExecutor() {
		return this.getAreaContext().getExecutor();
	}

	public void registerNativeListener(Listener listener);

	public void unregisterNativeListener(Listener listener);
}
