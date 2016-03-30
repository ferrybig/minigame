package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import io.netty.util.AttributeKey;
import io.netty.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;

public class WaitingPhase extends DefaultPhase {

	public static final AttributeKey<ScheduledFuture<?>> CURRENT_WAITING_LOOP
			= AttributeKey.valueOf(WaitingPhase.class, "timeout");
	private final long delayMiliseconds;

	public WaitingPhase(long delayMiliseconds) {
		this.delayMiliseconds = delayMiliseconds;
	}

	public WaitingPhase(long time, TimeUnit unit) {
		this(unit.toMillis(time));
	}

	@Override
	public void onPhaseUnload(PhaseContext area) throws Exception {
		super.onPhaseUnload(area);
		area.getAreaContext().attr(CURRENT_WAITING_LOOP).getAndRemove().cancel(false);
	}

	@Override
	public void onPhaseLoad(PhaseContext area) throws Exception {
		super.onPhaseLoad(area);
		area.getAreaContext().attr(CURRENT_WAITING_LOOP).set(
				area.getExecutor().schedule(() -> area.triggerNextPhase(),
						delayMiliseconds, TimeUnit.MILLISECONDS));
	}

}
