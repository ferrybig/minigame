package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.concurrent.Future;
import java.util.Objects;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;

public abstract class FilterAreaContextConstructor implements AreaContextConstructor {

	protected final AreaContextConstructor parent;

	public FilterAreaContextConstructor(AreaContextConstructor parent) {
		this.parent = Objects.requireNonNull(parent, "parent == null");
	}

	@Override
	public Future<AreaContext> construct(GameCore core, Area area, Controller controller, Pipeline pipeline) {
		return this.parent.construct(core, area, controller, pipeline);
	}

}
