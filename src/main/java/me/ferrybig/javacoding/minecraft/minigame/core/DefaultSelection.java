package me.ferrybig.javacoding.minecraft.minigame.core;

import java.util.Objects;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class DefaultSelection implements Selection {

	private final World world;
	private final Vector first = new Vector();
	private final Vector second = new Vector();

	public DefaultSelection(World world) {
		this.world = Objects.requireNonNull(world, "world == null");
	}

	@Override
	public DefaultSelection deepClone() {
		DefaultSelection n = new DefaultSelection(world);
		n.getFirstPoint().add(first);
		n.getSecondPoint().add(second);
		return n;
	}

	@Override
	public Vector getFirstPoint() {
		return first;
	}

	@Override
	public Vector getSecondPoint() {
		return second;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + Objects.hashCode(this.world);
		hash = 59 * hash + Objects.hashCode(this.first);
		hash = 59 * hash + Objects.hashCode(this.second);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DefaultSelection other = (DefaultSelection) obj;
		if (this.world != other.world && !Objects.equals(this.world, other.world)) {
			return false;
		}
		if (!Objects.equals(this.first, other.first)) {
			return false;
		}
		if (!Objects.equals(this.second, other.second)) {
			return false;
		}
		return true;
	}

}
