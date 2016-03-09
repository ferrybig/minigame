package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.EventExecutor;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.bukkit.plugin.Plugin;

public interface InformationContext {

	public TranslationMap getTranslations();

	public Logger getLogger();

	public EventExecutor getExecutor();

	public AreaConstructor getAreaConstructor();

	public AreaContextConstructor getAreaContextConstructor();

	public AreaVerifier getAreaVerifier();

	public Plugin getPlugin();

}
