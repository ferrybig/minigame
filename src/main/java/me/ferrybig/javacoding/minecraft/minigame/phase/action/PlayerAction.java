package me.ferrybig.javacoding.minecraft.minigame.phase.action;

import java.util.function.BiPredicate;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.phase.Phase;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;

public abstract class PlayerAction {

	protected abstract void trigger(PhaseContext phase, Player player);

	private void tryTrigger(PhaseContext phase, Player player) {
		phase.safeCall(() -> trigger(phase, player), this);
	}

	public Phase onDeath() {
		return new ListenerPhase() {
			@EventHandler
			public void onEvent(PlayerDeathEvent evt) {
				if (shouldHandle(evt.getEntity())) {
					tryTrigger(getPhaseContext(), evt.getEntity());
				}
			}
		};
	}

	public Phase onJoin() {
		return new SkippedPhase() {
			@Override
			public void onPlayerJoin(PhaseContext area, PlayerJoinMessage player) throws Exception {
				player.addSuccessListener(() -> tryTrigger(area, player.getPlayer()));
				super.onPlayerJoin(area, player);
			}

		};
	}

	public Phase onLeave() {
		return new SkippedPhase() {
			@Override
			public void onPlayerLeave(PhaseContext area, PlayerLeaveMessage player) throws Exception {
				tryTrigger(area, player.getPlayer());
				super.onPlayerLeave(area, player);
			}

		};
	}

	public Phase onSpectate() {
		return new SkippedPhase() {
			@Override
			public void onPlayerSpectate(PhaseContext area, PlayerSpectateMessage player) throws Exception {
				tryTrigger(area, player.getPlayer());
				super.onPlayerSpectate(area, player);
			}

		};
	}

	public Phase onAdvance() {
		return new SkippedPhase() {
			@Override
			public void onPhaseRegister(PhaseContext area) throws Exception {
				for (Player p : area.getController().getPlayers().keySet()) {
					tryTrigger(area, p);
				}
				super.onPhaseRegister(area);
			}

		};
	}

	public PlayerAction withFilter(BiPredicate<PhaseContext, Player> filter) {
		PlayerAction self = this;
		return new PlayerAction() {
			@Override
			protected void trigger(PhaseContext phase, Player player) {
				if (filter.test(phase, player)) {
					self.tryTrigger(phase, player);
				}
			}
		};
	}

}
