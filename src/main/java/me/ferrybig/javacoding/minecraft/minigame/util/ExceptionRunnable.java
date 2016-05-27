package me.ferrybig.javacoding.minecraft.minigame.util;

import java.util.concurrent.Callable;

public interface ExceptionRunnable {

	public void run() throws Exception;

	public static ExceptionRunnable fromCallable(Callable<?> call) {
		return ()->{
			call.call();
		};
	}
}
