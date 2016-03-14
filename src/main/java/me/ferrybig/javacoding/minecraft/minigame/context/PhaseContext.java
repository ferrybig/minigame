package me.ferrybig.javacoding.minecraft.minigame.context;

import java.util.concurrent.ExecutorService;
import me.ferrybig.javacoding.minecraft.minigame.Triggerable;
import org.bukkit.event.Listener;

public interface PhaseContext extends Triggerable {

	public AreaContext getAreaContext();

	public default ExecutorService getExecutor() {
		return this.getAreaContext().getExecutor();
	}

	public void registerNativeListener(Listener listener);

	public void unregisterNativeListener(Listener listener);
}
