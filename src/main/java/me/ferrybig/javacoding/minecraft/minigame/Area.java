package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import java.util.function.Consumer;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.information.ResolvedAreaInformation;

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
