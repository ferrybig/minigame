
package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;

public interface AreaConstructor {

	public Area construct(ResolvedAreaInformation info);
}
