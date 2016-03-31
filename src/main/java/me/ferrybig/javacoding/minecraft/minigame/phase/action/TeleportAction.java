package me.ferrybig.javacoding.minecraft.minigame.phase.action;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.Phase;
import me.ferrybig.javacoding.minecraft.minigame.phase.impl.ListenerPhase;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;

public class TeleportAction extends PlayerAction {

	private final Function<PhaseContext, Location> location;

	protected TeleportAction(Function<PhaseContext, Location> location) {
		this.location = Objects.requireNonNull(location, "location == null");
	}

	public TeleportAction(Supplier<Location> location) {
		this(generateLamba(location));
	}

	public TeleportAction(Location location) {
		this(generateLamba(location));
	}

	public TeleportAction(String name) {
		this(name, DistributionType.ROUND_ROBIN);
	}

	public TeleportAction(String name, DistributionType type) {
		this(generateLamba(name, type));
	}

	@Override
	protected void trigger(PhaseContext phase, Player player) {
		player.teleport(location.apply(phase));
	}

	@Override
	public Phase onRespawn() {
		return new RespawnListener();
	}

	private static Function<PhaseContext, Location> generateLamba(Supplier<Location> loc) {
		Objects.requireNonNull(loc, "loc == null");
		return i -> loc.get();
	}

	private static Function<PhaseContext, Location> generateLamba(Location loc) {
		Objects.requireNonNull(loc, "loc == null");
		return i -> loc;
	}

	private static Function<PhaseContext, Location> generateLamba(
			String name, DistributionType type) {
		Objects.requireNonNull(name, "name == null");
		Objects.requireNonNull(type, "type == null");
		Function<List<Location>, Location> selector = type.getSelector();
		return (PhaseContext t) -> {
			List<Location> locations = t.getAreaContext().getTaggedLocations(name);
			if (locations == null || locations.isEmpty()) {
				throw new NoSuchElementException("No locations found with tag: " + name);
			}
			return selector.apply(locations);
		};
	}

	public enum DistributionType {

		PURE_RANDOM() {
			@Override
			public Function<List<Location>, Location> getSelector() {
				return l -> l.get(random.nextInt(l.size()));
			}

		},
		ROUND_ROBIN() {
			@Override
			public Function<List<Location>, Location> getSelector() {
				return new Function<List<Location>, Location>() {
					int counter = -1;

					@Override
					public Location apply(List<Location> l) {
						if (counter == -1) {
							counter = random.nextInt(l.size());
						} else if (counter >= l.size()) {
							counter = 0;
						}
						Location r = l.get(counter);
						counter++;
						return r;
					}
				};
			}

		},;
		private static final Random random = new Random();

		public abstract Function<List<Location>, Location> getSelector();
	}

	@SuppressFBWarnings(value = "")
	private class RespawnListener extends ListenerPhase {

		public RespawnListener() {
		}

		@EventHandler
		public void onRespawn(PlayerRespawnEvent evt) {
			evt.setRespawnLocation(location.apply(getPhaseContext()));
		}
	}

}
