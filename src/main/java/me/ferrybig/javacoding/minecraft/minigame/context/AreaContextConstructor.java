package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.information.ResolvedAreaInformation;

public interface AreaContextConstructor {

	public Future<AreaContext> construct(GameCore core, ResolvedAreaInformation area,
			Controller controller, Pipeline pipeline);
}
