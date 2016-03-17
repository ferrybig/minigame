package me.ferrybig.javacoding.minecraft.minigame.phase;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.Controller.PlayerInfo;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class DamageTogglePhase extends DefaultPhase {

	private final static AttributeKey<Set<DamageType>> TYPE
			= AttributeKey.valueOf(DamageTogglePhase.class, "type");
	private Listener listener;
	private final Set<DamageType> newTypes;

	public DamageTogglePhase(DamageType... newTypes) {
		this.newTypes = EnumSet.noneOf(DamageType.class);
		this.newTypes.addAll(Arrays.asList(newTypes));
	}

	@Override
	public void afterReset(PhaseContext area) {
		if(listener != null)
			area.unregisterNativeListener(listener);
		super.afterReset(area);
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		Attribute<Set<DamageType>> type = area.getAreaContext().attr(TYPE);
		if (type.get() == null) {
			AreaContext ar = area.getAreaContext();
			listener = new Listener() {
				@EventHandler
				public void onDamage(EntityDamageByEntityEvent event) {
					Entity defender = event.getEntity();
					Entity attacker = event.getDamager();
					if (attacker instanceof Projectile) {
						Projectile projectile = (Projectile) attacker;
						if (projectile.getShooter() instanceof Entity) {
							attacker = (Entity) projectile.getShooter();
						}
					}
					if (attacker instanceof Player && defender instanceof Player) {
						onPlayerVSPlayerDamage(ar, (Player) attacker, (Player) defender, event);
					}
				}
			};
			area.registerNativeListener(listener);
		}
		type.set(newTypes);
	}

	private void onPlayerVSPlayerDamage(AreaContext area,
			Player attacker, Player defender, Cancellable event) {
		Optional<PlayerInfo> attackerInfo = area.getController().getPlayer(attacker);
		Optional<PlayerInfo> defenderInfo = area.getController().getPlayer(defender);
		if(attackerInfo.isPresent() == false && defenderInfo.isPresent() == false) {
			// Not intrested in this condition
		} else if(attackerInfo.isPresent() == true && defenderInfo.isPresent() == true) {
			if(attacker.equals(defender)) {
				if(!isDamageAllowed(area, DamageType.SELF)) {
					event.setCancelled(true);
				}
			} else {
				PlayerInfo att = attackerInfo.get();
				PlayerInfo def = defenderInfo.get();
				if(att.isSpectator() || def.isSpectator()) {
					if(!isDamageAllowed(area, DamageType.SPECTATOR)) {
						event.setCancelled(true);
					}
				} else {
					Optional<String> attTeam = att.getTeam();
					Optional<String> defTeam = def.getTeam();
					if (attTeam.isPresent() && attTeam.equals(defTeam)) {
						if(!isDamageAllowed(area, DamageType.TEAM)) {
							event.setCancelled(true);
						}
					} else {
						if(!isDamageAllowed(area, DamageType.NORMAL)) {
							event.setCancelled(true);
						}
					}
				}
			}
		} else {
			if(!area.attr(TYPE).get().contains(DamageType.OUTSIDE)) {
				event.setCancelled(true);
			}
		}
	}

	public static boolean isDamageAllowed(AreaContext c, DamageType type) {
		return c.attr(TYPE).get().contains(type);
	}

	public enum DamageType {
		SPECTATOR,
		TEAM,
		NORMAL,
		OUTSIDE,
		SELF,
	}

}
