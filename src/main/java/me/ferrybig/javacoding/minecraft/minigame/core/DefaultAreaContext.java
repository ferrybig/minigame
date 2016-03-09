package me.ferrybig.javacoding.minecraft.minigame.core;

import io.netty.util.DefaultAttributeMap;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformationBuilder;
import me.ferrybig.javacoding.minecraft.minigame.Controller;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Pipeline;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class DefaultAreaContext extends DefaultAttributeMap implements AreaContext {

	private static final AtomicLong NEXT_INSTANCE_ID = new AtomicLong();
	private final long id = NEXT_INSTANCE_ID.getAndIncrement();
	private final GameCore core;
	private final Area area;
	private final Controller controller;
	private final AreaInformation information;
	private final Pipeline pipeline;
	private final boolean valid;
	private final Set<String> teams;

	public DefaultAreaContext(GameCore core, Area area,
			Pipeline pipeline, Controller controller) {
		Objects.requireNonNull(area, "area == null");
		this.core = core;
		this.area = area;
		this.information = AreaInformationBuilder.from(area).setUneditable().create();
		this.valid = area.isValid();
		this.teams = Collections.unmodifiableSet(new LinkedHashSet<>(area.validTeams()));
		this.pipeline = pipeline;
		this.controller = controller;
	}

	@Override
	public GameCore getCore() {
		return core;
	}

	@Override
	public ExecutorService getExecutor() {
		return getCore().getInfo().getExecutor();
	}

	@Override
	public long instanceId() {
		return id;
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
	public Area getArea() {
		return area;
	}

	@Override
	public Set<String> validTeams() {
		return teams;
	}

	@Override
	public Controller getController() {
		return controller;
	}

	@Override
	public Pipeline pipeline() {
		return pipeline;
	}

}
