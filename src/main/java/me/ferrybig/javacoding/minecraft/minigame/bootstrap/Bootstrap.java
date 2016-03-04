package me.ferrybig.javacoding.minecraft.minigame.bootstrap;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.AreaConstructor;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.listener.GameListener;
import me.ferrybig.javacoding.minecraft.minigame.configuration.FullConfig;
import org.bukkit.plugin.Plugin;

public interface Bootstrap {

	public Bootstrap withCoreAccessor(CoreAccessor accessor);

	public Bootstrap withExecutor(EventExecutor executor);

	public Bootstrap withPlugin(Plugin plugin);

	public Bootstrap withGlobalGameListener(GameListener listen);

	public Bootstrap withConfig(FullConfig config);

	public Bootstrap withAreaVerifier(AreaVerifier verifier);

	public Bootstrap withAreaConstructor(AreaConstructor constructor);

	public Future<GameCore> build();

}
