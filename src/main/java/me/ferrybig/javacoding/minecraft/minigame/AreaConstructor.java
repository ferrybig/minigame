package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import java.util.function.Function;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.information.ResolvedAreaInformation;

public interface AreaConstructor {

	public Area construct(ResolvedAreaInformation info,
			Function<Area, AreaCreator> editArea,
			Function<Area, Future<AreaContext>> contextCreator);
}
