package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;

public interface AreaContextConstructor {

	public Future<AreaContext> construct(GameCore core, ResolvedAreaInformation area,
			Controller controller, Pipeline pipeline);
}
