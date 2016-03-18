package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.concurrent.EventExecutor;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.Triggerable;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translation;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.event.Listener;

public interface PhaseContext extends Triggerable, Translator {

	public AreaContext getAreaContext();

	public default EventExecutor getExecutor() {
		return this.getAreaContext().getExecutor();
	}

	public default Controller getController() {
		return this.getAreaContext().getController();
	}

	public void registerNativeListener(Listener listener);

	public void unregisterNativeListener(Listener listener);

	@Override
	public default String translate(Translation translation, Object ... args) {
		return getAreaContext().getCore().getInfo().getTranslations().translate(translation, args);
	}
}
