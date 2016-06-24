package me.ferrybig.javacoding.minecraft.minigame.context;

import io.netty.util.concurrent.Future;
import java.util.Objects;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;

/**
 * Simple class that delegates its event to a parent class
 * @author Fernando
 */
public abstract class FilterAreaContextConstructor implements AreaContextConstructor {

	protected final AreaContextConstructor parent;

	/**
	 * COnstructs a FilterAreaContextConstructor
	 * @param parent AreaContextConstructor to delegate calls to, may not be null
	 */
	public FilterAreaContextConstructor(AreaContextConstructor parent) {
		this.parent = Objects.requireNonNull(parent, "parent == null");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Future<AreaContext> construct(GameCore core, Area area, Controller controller, Pipeline pipeline) {
		return this.parent.construct(core, area, controller, pipeline);
	}

}
