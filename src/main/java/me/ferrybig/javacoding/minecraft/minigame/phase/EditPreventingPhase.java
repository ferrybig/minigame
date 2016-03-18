package me.ferrybig.javacoding.minecraft.minigame.phase;

import java.util.Optional;
import me.ferrybig.javacoding.minecraft.minigame.Controller.PlayerInfo;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEvent;

public class EditPreventingPhase extends DefaultPhase {

	private Listener listener;
	private PhaseContext area;

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		super.onPhaseUnregister(area);
		area.unregisterNativeListener(listener);
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		this.area = area;
		this.listener = new EditListener();
		area.registerNativeListener(listener);
		area.triggerNextPhase();
	}

	private class EditListener implements Listener {

		@EventHandler
		public void onEvent(BlockPlaceEvent evt) {
			Optional<PlayerInfo> player = area.getController().getPlayer(evt.getPlayer());
			if (!player.isPresent()) {
				return;
			}
			evt.setCancelled(true);
		}

		@EventHandler
		public void onEvent(BlockBreakEvent evt) {
			Optional<PlayerInfo> player = area.getController().getPlayer(evt.getPlayer());
			if (!player.isPresent()) {
				return;
			}
			evt.setCancelled(true);
		}

		@EventHandler
		public void onEvent(PlayerBucketEvent evt) {
			Optional<PlayerInfo> player = area.getController().getPlayer(evt.getPlayer());
			if (!player.isPresent()) {
				return;
			}
			evt.setCancelled(true);
		}
	}

}
