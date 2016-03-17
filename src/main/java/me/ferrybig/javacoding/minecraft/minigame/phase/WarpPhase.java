package me.ferrybig.javacoding.minecraft.minigame.phase;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.AreaException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WarpPhase extends DefaultPhase {

	private static final Random RANDOM = new Random();
	private final String locationName;
	private final RandomOrder order;

	public WarpPhase(String locationName) {
		this(locationName, RandomOrder.ROUND_ROBIN);
	}

	public WarpPhase(String locationName, RandomOrder order) {
		this.locationName = Objects.requireNonNull(locationName, "locationName == null");
		this.order = Objects.requireNonNull(order, "order == null");
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		super.onPhaseRegister(area);
		List<Location> loc = area.getAreaContext().getTaggedLocations(locationName);
		if (loc.isEmpty()) {
			throw new AreaException(
					"Missing location " + locationName + " in " + area.getAreaContext());
		}
		Collection<Player> players = area.getAreaContext().getController().getPlayers().keySet();
		switch (order) {
			case RANDOM: {
				for (Player p : players) {
					p.teleport(loc.get(RANDOM.nextInt(loc.size())));
				}
			}
			break;
			case FIRST_ONLY: {
				for (Player p : players) {
					p.teleport(loc.get(0));
				}
			}
			break;
			case ROUND_ROBIN: {
				Iterator<Player> playerItr = players.iterator();
				Iterator<Location> locationItr = loc.iterator();
				while (playerItr.hasNext()) {
					if (!locationItr.hasNext()) {
						locationItr = loc.iterator();
					}
					playerItr.next().teleport(locationItr.next());
				}
			}
			break;
			case RANDOM_ROUND_ROBIN: {
				Iterator<Player> playerItr = players.iterator();
				Iterator<Location> locationItr = loc.listIterator(RANDOM.nextInt(loc.size()));
				while (playerItr.hasNext()) {
					if (!locationItr.hasNext()) {
						locationItr = loc.iterator();
					}
					playerItr.next().teleport(locationItr.next());
				}
			}
			break;
			default:
				throw new AssertionError("Unknown order: " + order);
		}
		area.triggerNextPhase();
	}

	public enum RandomOrder {
		RANDOM,
		FIRST_ONLY,
		ROUND_ROBIN,
		RANDOM_ROUND_ROBIN,
	}
}
