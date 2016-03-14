package me.ferrybig.javacoding.minecraft.minigame.information;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

public interface AreaInformation {
	
	public boolean isEnabled();

	public String getName();
	
	public String getDescription();

	public Selection getBounds();
	
	public Map<String, List<Block>> getTaggedBlocks();

	public default List<Block> getTaggedBlocks(String tag) {
		
		List<Block> get = getTaggedBlocks().get(tag);
		if(get == null)
			return Collections.emptyList();
		return get;
	}
	
	public Map<String, List<Location>> getTaggedLocations();

	public default List<Location> getTaggedLocations(String tag) {
		List<Location> get = getTaggedLocations().get(tag);
		if(get == null)
			return Collections.emptyList();
		return get;
	}

	public int maxPlayers();
	
	public default boolean canBeUsed() {
		return isEnabled();
	}
	
	public default AreaInformation getInformationCopy() {
		boolean enabled = isEnabled();
		String name = getName();
		String description = getDescription();
		Selection selection = getBounds().deepClone();
		int maxPlayers = maxPlayers();
		Map<String,List<Location>> taggedLocations = new HashMap<>(getTaggedLocations());
		taggedLocations.replaceAll((k,v)->new ArrayList<>(v));
		Map<String,List<Block>> taggedBlocks = new HashMap<>(getTaggedBlocks());
		taggedBlocks.replaceAll((k,v)->new ArrayList<>(v));
		return new AreaInformation() {
			@Override
			public Selection getBounds() {
				return selection;
			}

			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public String getName() {
				return name;
			}

			@Override
			public Map<String, List<Block>> getTaggedBlocks() {
				return taggedBlocks;
			}

			@Override
			public Map<String, List<Location>> getTaggedLocations() {
				return taggedLocations;
			}

			@Override
			public boolean isEnabled() {
				return enabled;
			}

			@Override
			public int maxPlayers() {
				return maxPlayers;
			}
		};
	}
	
	public static <T> Map<String, List<T>> copyTaggedInformation(
			Map<String, List<T>> info, boolean unmodifeable) {
		Map<String, List<T>> t;
		if(info != null)
			t = new HashMap<>(info);
		else
			t = new HashMap<>();
		t.replaceAll((k,v)->new ArrayList<>(v));
		if (!unmodifeable) {
			return t;
		}
		t.replaceAll((k,v)->Collections.unmodifiableList(v));
		return Collections.unmodifiableMap(t);
	}
}
