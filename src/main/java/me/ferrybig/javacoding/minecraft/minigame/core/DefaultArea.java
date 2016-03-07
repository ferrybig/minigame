package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.concurrent.Future;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformationBuilder;
import me.ferrybig.javacoding.minecraft.minigame.InformationContext;
import me.ferrybig.javacoding.minecraft.minigame.ResolvedAreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class DefaultArea implements Area {

	private final Function<Area, Future<AreaContext>> contextCreator;
	private final Function<Area, AreaCreator> editArea;
	private final AreaInformation information;
	private final boolean valid;
	private final Set<String> teams;

	public DefaultArea(ResolvedAreaInformation information,
			Function<Area, AreaCreator> editArea,
			Function<Area, Future<AreaContext>> contextCreator) {
		Objects.requireNonNull(information, "information == null");
		this.information = AreaInformationBuilder.from(information).setUneditable().create();
		this.valid = information.isValid();
		this.teams = Collections.unmodifiableSet(new LinkedHashSet<>(information.validTeams()));
		this.editArea = Objects.requireNonNull(editArea, "editArea == null");
		this.contextCreator = Objects.requireNonNull(contextCreator, "contextCreator == null");
	}

	@Override
	public AreaCreator editArea() {
		return editArea.apply(this);
	}

	@Override
	public boolean isEnabled() {
		return information.isEnabled();
	}

	@Override
	public String getName() {
		return information.getName();
	}

	@Override
	public String getDescription() {
		return information.getDescription();
	}

	@Override
	public Selection getBounds() {
		return information.getBounds().deepClone();
	}

	@Override
	public Map<String, List<Block>> getTaggedBlocks() {
		return information.getTaggedBlocks();
	}

	@Override
	public List<Block> getTaggedBlocks(String tag) {
		return information.getTaggedBlocks(tag);
	}

	@Override
	public Map<String, List<Location>> getTaggedLocations() {
		return information.getTaggedLocations();
	}

	@Override
	public List<Location> getTaggedLocations(String tag) {
		return information.getTaggedLocations(tag);
	}

	@Override
	public boolean isValid() {
		return valid;
	}

	@Override
	public int maxPlayers() {
		return information.maxPlayers();
	}

	@Override
	public Future<AreaContext> newInstance() {
		return contextCreator.apply(this);
	}

	@Override
	public Set<String> validTeams() {
		return teams;
	}

	@Override
	public ResolvedAreaInformation getInformationCopy() {
		return this;
	}

}
