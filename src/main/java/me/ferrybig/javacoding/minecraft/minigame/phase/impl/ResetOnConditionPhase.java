package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import java.util.function.Predicate;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;

public class ResetOnConditionPhase extends DefaultPhase {

	private final Predicate<PhaseContext> condition;

	public ResetOnConditionPhase(Predicate<PhaseContext> condition) {
		this.condition = condition;
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		if (condition.test(area)) {
			area.triggerReset();
		} else {
			area.triggerNextPhase();
		}
	}

}
