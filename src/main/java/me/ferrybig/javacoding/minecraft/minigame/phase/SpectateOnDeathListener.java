package me.ferrybig.javacoding.minecraft.minigame.phase;

import java.util.Optional;
import me.ferrybig.javacoding.minecraft.minigame.Controller.PlayerInfo;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class SpectateOnDeathListener extends DefaultPhase {

	private Listener listener;
	private PhaseContext area;
	private final GameMode gamemode;

	public SpectateOnDeathListener() {
		this(null);
	}
	
	public SpectateOnDeathListener(GameMode gamemode) {
		this.gamemode = gamemode;
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		super.onPhaseUnregister(area);
		area.unregisterNativeListener(listener);
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		this.area = area;
		this.listener = new SpectateListener();
		area.registerNativeListener(listener);
		area.triggerNextPhase();
	}

	private class SpectateListener implements Listener {

		@EventHandler
		public void playerDeathEvent(PlayerDeathEvent evt) {
			Optional<PlayerInfo> player = area.getController().getPlayer(evt.getEntity());
			if(!player.isPresent())
				return;
			PlayerInfo info = player.get();
			if (!info.isSpectator()) {
				info.setSpectator(true);
				if(gamemode != null) {
					evt.getEntity().setGameMode(gamemode);
				}
			}
		}
	}

}
