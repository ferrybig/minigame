package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import java.util.function.Consumer;

public interface Area extends ResolvedAreaInformation {

	public Future<AreaContext> newInstance();

	@Deprecated
	public default Future<AreaContext> newInstance(Consumer<AreaContext> decorator) {
		Future<AreaContext> inst = newInstance();
		inst.addListener(f -> {
			if (f.isSuccess()) {
				decorator.accept(inst.get());
			}
		});
		return inst;
	}

	public AreaCreator editArea();
}
