package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.concurrent.Future;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;

public interface AreaContextConstructor {

	public Future<AreaContext> construct(GameCore core, Area area,
			Controller controller, Pipeline pipeline);
}
