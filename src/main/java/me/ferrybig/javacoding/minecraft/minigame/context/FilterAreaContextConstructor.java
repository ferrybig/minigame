package me.ferrybig.javacoding.minecraft.minigame.context;

public abstract class FilterAreaContextConstructor implements AreaContextConstructor {

	protected final AreaContextConstructor parent;

	public FilterAreaContextConstructor(AreaContextConstructor parent) {
		this.parent = parent;
	}

}
