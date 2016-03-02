
package me.ferrybig.javacoding.minecraft.minigame;

import io.netty.util.concurrent.Future;
import org.bukkit.Location;

public interface AreaConstructor {

	public Future<Area> construct(Location buildingLoc, ResolvedAreaInformation info);
}
