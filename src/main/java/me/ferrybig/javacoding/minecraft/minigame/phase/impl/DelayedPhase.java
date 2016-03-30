package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;

public abstract class DelayedPhase extends SkippedPhase {

	private ScheduledFuture<?> future;
	private final long timeout;
	private final TimeUnit unit;

	public DelayedPhase(long timeout, TimeUnit unit) {
		this.timeout = timeout;
		this.unit = unit;
	}

	protected abstract void trigger(PhaseContext area) throws Exception;

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		this.future = area.getExecutor().schedule(() -> {
			trigger(area);
			return null;
		}, timeout, unit);
		super.onPhaseRegister(area);
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		this.future.cancel(false);
		super.onPhaseUnregister(area);
	}

}
