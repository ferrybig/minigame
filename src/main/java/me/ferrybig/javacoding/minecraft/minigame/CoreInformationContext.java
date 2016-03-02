package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.EventExecutor;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;

public interface CoreInformationContext {

	public TranslationMap getTranslations();
	
	public Logger getLogger();
	
	public EventExecutor getExecutor();
}
