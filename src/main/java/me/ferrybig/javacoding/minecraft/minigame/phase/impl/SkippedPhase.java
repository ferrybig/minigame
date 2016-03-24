
package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;

public class SkippedPhase extends DefaultPhase {

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		area.triggerNextPhase();
	}

}
