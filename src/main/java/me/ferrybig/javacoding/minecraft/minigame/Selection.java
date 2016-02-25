package me.ferrybig.javacoding.minecraft.minigame;

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
