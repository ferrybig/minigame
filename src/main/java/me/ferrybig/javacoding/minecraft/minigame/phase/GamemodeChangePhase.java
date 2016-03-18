
package me.ferrybig.javacoding.minecraft.minigame.phase;

import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.GameMode;

public class GamemodeChangePhase extends DefaultPhase {

	private final GameMode newMode;

	public GamemodeChangePhase(GameMode newMode) {
		this.newMode = newMode;
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		area.getController().getPlayers().keySet().forEach(p->p.setGameMode(newMode));
		area.triggerNextPhase();
	}

}
