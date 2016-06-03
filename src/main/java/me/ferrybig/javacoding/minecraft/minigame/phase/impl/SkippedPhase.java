package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;

/**
 * Abstract phase that skips to the next phase
 * @author Fernando
 */
public abstract class SkippedPhase extends DefaultPhase {

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		area.triggerNextPhase();
	}

}
