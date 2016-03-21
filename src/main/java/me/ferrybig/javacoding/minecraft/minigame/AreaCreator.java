package me.ferrybig.javacoding.minecraft.minigame;

import java.util.List;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import static me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation.copyTaggedInformation;
import me.ferrybig.javacoding.minecraft.minigame.information.ResolvedAreaInformation;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface AreaCreator extends ResolvedAreaInformation {

	public AreaCreator setDescription(String description);

	public AreaCreator setMaxPlayers(int maxPlayers);

	public AreaCreator setName(String name);

	public AreaCreator setEnabled(boolean enabled);

	public AreaCreator setSelection(Selection selection);

	public AreaCreator setTaggedBlocks(Map<String, List<Block>> taggedBlocks);

	public AreaCreator setTaggedLocations(Map<String, List<Location>> taggedLocations);

	public Area createArea();

	public default AreaCreator copyInformation(AreaInformation info) {
		setDescription(info.getDescription());
		setMaxPlayers(info.maxPlayers());
		setName(info.getName());
		setSelection(info.getBounds().deepClone());
		setTaggedBlocks(copyTaggedInformation(info.getTaggedBlocks(), false));
		setTaggedLocations(copyTaggedInformation(info.getTaggedLocations(), false));
		return this;
	}

}
