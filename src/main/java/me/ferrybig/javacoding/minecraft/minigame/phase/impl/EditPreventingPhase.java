package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import java.util.Optional;
import me.ferrybig.javacoding.minecraft.minigame.Controller.PlayerInfo;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.DefaultPhase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;

public class EditPreventingPhase extends DefaultPhase {

	private Listener listener;
	private PhaseContext area;

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		area.unregisterNativeListener(listener);
		super.onPhaseUnregister(area);
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
		public void onEvent(HangingBreakByEntityEvent evt) {
			if(evt.getRemover() instanceof Player) {
				Optional<PlayerInfo> player = area.getController().getPlayer((Player)evt.getRemover());
				if (!player.isPresent()) {
					return;
				}
				evt.setCancelled(true);
			}
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
		public void onEvent(PlayerBucketEmptyEvent evt) {
			Optional<PlayerInfo> player = area.getController().getPlayer(evt.getPlayer());
			if (!player.isPresent()) {
				return;
			}
			evt.setCancelled(true);
		}

		@EventHandler
		public void onEvent(PlayerBucketFillEvent evt) {
			Optional<PlayerInfo> player = area.getController().getPlayer(evt.getPlayer());
			if (!player.isPresent()) {
				return;
			}
			evt.setCancelled(true);
		}
	}

}
