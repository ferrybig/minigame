package me.ferrybig.javacoding.minecraft.minigame;

import me.ferrybig.javacoding.minecraft.minigame.core.DefaultSelection;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public interface Selection {

	public World getWorld();

	public Vector getFirstPoint();

	public default Block getFirstPointAsBlock() {
		Vector l = getFirstPoint();
		return getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public default Location getFirstPointAsLocation() {
		return getFirstPoint().toLocation(getWorld());
	}
	
	public default void setFirstPoint(Vector n) {
		Vector first = getFirstPoint();
		first.zero();
		first.add(n);
	}

	public Vector getSecondPoint();

	public default Block getSecondPointAsBlock() {
		Vector l = getSecondPoint();
		return getWorld().getBlockAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
	}

	public default Location getSecondPointAsLocation() {
		return getSecondPoint().toLocation(getWorld());
	}
	
	public default void setSecondPoint(Vector n) {
		Vector second = getSecondPoint();
		second.zero();
		second.add(n);
	}
	
	public default boolean isInArea(Location loc) {
		return isInArea(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
	}
	
	public default boolean isInArea(Block block) {
		return isInArea(block.getWorld(), block.getX(), block.getY(), block.getZ());
	}

	public default boolean isInArea(World w, double x, double y, double z) {
		if(!this.getWorld().equals(w)) {
			return false;
		}
		Vector lowest = getFirstPoint();
		Vector highest = getSecondPoint();

		double lowestX = Math.min(lowest.getX(), highest.getX());
		double highestX = Math.max(lowest.getX(), highest.getX());

		double lowestY = Math.min(lowest.getY(), highest.getY());
		double highestY = Math.max(lowest.getY(), highest.getY());

		double lowestZ = Math.min(lowest.getZ(), highest.getZ());
		double highestZ = Math.max(lowest.getZ(), highest.getZ());

		return lowestX <= x && x <= highestX &&
				lowestZ <= z && z <= highestZ &&
				lowestY <= y && y <= highestY;
	}

	public Selection deepClone();

	public default void normalize() {
		Vector lowest = getFirstPoint();
		Vector highest = getSecondPoint();

		double lowestX = Math.min(lowest.getX(), highest.getX());
		highest.setX(Math.max(lowest.getX(), highest.getX()));
		lowest.setX(lowestX);

		double lowestY = Math.min(lowest.getY(), highest.getY());
		highest.setY(Math.max(lowest.getY(), highest.getY()));
		lowest.setY(lowestY);

		double lowestZ = Math.min(lowest.getZ(), highest.getZ());
		highest.setZ(Math.max(lowest.getZ(), highest.getZ()));
		lowest.setZ(lowestZ);
	}

	public static Selection newInstance(World w) {
		return new DefaultSelection(w);
	}

}
