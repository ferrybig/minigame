package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;

public interface AreaContextConstructor {

	public Future<AreaContext> construct(ResolvedAreaInformation area,
			Controller controller, Pipeline pipeline);
}
