package me.ferrybig.javacoding.minecraft.minigame;

import java.util.function.Consumer;

public interface Area extends ResolvedAreaInformation {

	public AreaContext newInstance();

	public default AreaContext newInstance(Consumer<AreaContext> decorator) {
		AreaContext inst = newInstance();
		decorator.accept(inst);
		return inst;
	}

	public AreaCreator editArea();
}
