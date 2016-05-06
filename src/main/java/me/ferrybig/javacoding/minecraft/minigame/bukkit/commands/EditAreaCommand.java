package me.ferrybig.javacoding.minecraft.minigame.bukkit.commands;

import com.google.common.collect.MapMaker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import me.ferrybig.javacoding.minecraft.minigame.translation.BaseTranslation;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;

/**
 *
 * @author Fernando
 */
public class EditAreaCommand extends BasicCommand {

	private final Map<String, AreaCreator> pendingAreas = new HashMap<>();
	private final ConcurrentMap<CommandSender, String> playerAreas
			= new MapMaker().weakKeys().concurrencyLevel(1).makeMap();
	private final Map<Action, Map<Target, BiConsumer<CommandSender, List<String>>>> executors = new HashMap<>();

	public EditAreaCommand(Supplier<GameCore> core, BooleanSupplier coreEnabled) {
		super(core, coreEnabled);
		addCommand(Action.LIST, Target.BLOCKS, (s, a) -> {
			AreaCreator creator = getEditContext(s);
			if (creator == null) {
				s.sendMessage("You are not in a edit context");
				return;
			}
			s.sendMessage("The following blocks are defined for this area:");
			for (Entry<String, List<Block>> entry : creator.getTaggedBlocks().entrySet()) {
				s.sendMessage("Section " + entry.getKey());
				int i = 0;
				for (Block b : entry.getValue()) {
					s.sendMessage(" - " + i++ + " " + b.getX() + " " + b.getY() + " " + b.getZ()
							+ " (" + b.getType() + ") [" + b.getWorld().getName() + "]");
				}
			}
		});
		addCommand(Action.LIST, Target.LOCATION, (s, a) -> {
			AreaCreator creator = getEditContext(s);
			if (creator == null) {
				s.sendMessage("You are not in a edit context");
				return;
			}
			s.sendMessage("The following locations are defined for this area:");
			for (Entry<String, List<Location>> entry : creator.getTaggedLocations().entrySet()) {
				s.sendMessage("Section " + entry.getKey());
				int i = 0;
				for (Location b : entry.getValue()) {
					s.sendMessage(" - " + i++ + " " + b.getX() + " " + b.getY() + " " + b.getZ()
							+ " (" + b.getYaw() + " - " + b.getPitch() + ") "
							+ "[" + b.getWorld().getName() + "]");
				}
			}
		});
		addCommand(Action.LIST, Target.MAXPLAYERS, (s, a) -> {
			AreaCreator creator = getEditContext(s);
			if (creator == null) {
				s.sendMessage("You are not in a edit context");
				return;
			}
			s.sendMessage("The max players of this area is: " + creator.maxPlayers());
		});
		addCommand(Action.LIST, Target.DESCRIPTION, (s, a) -> {
			AreaCreator creator = getEditContext(s);
			if (creator == null) {
				s.sendMessage("You are not in a edit context");
				return;
			}
			s.sendMessage("The description of this area is: " + creator.getDescription());
		});
		addCommand(Action.LIST, Target.ENABLED, (s, a) -> {
			AreaCreator creator = getEditContext(s);
			if (creator == null) {
				s.sendMessage("You are not in a edit context");
				return;
			}
			// TODO In translation file, use choiceformat
			// {0,choice,0#seconds|1#second|1<seconds}
			s.sendMessage("The state of this area is: " + creator.isEnabled());
		});
		addCommand(Action.LIST, Target.BOUNDS, (s, a) -> {
			AreaCreator creator = getEditContext(s);
			if (creator == null) {
				s.sendMessage("You are not in a edit context");
				return;
			}
			Selection bounds = creator.getBounds();
			Vector p1 = bounds.getFirstPoint();
			Vector p2 = bounds.getSecondPoint();
			s.sendMessage("World: " + bounds.getWorld().getName());
			s.sendMessage("First point: " + p1.getX() + " " + p1.getY() + " " + p1.getZ());
			s.sendMessage("Second point: " + p2.getX() + " " + p2.getY() + " " + p2.getZ());
		});
		addCommand(Action.LIST, Target.DESCRIPTION, (s, a) -> {
			AreaCreator creator = getEditContext(s);
			if (creator == null) {
				s.sendMessage("You are not in a edit context");
				return;
			}
			s.sendMessage("The description of this area is: " + creator.getDescription());
		});
	}

	private AreaCreator getEditContext(CommandSender sender) {
		String name = playerAreas.get(sender);
		if (name != null) {
			AreaCreator pending = pendingAreas.get(name);
			if (pending == null) {
				playerAreas.remove(sender, name);
			} else {
				return pending;
			}
		}
		return null;
	}

	private void putEditContext(CommandSender sender, AreaCreator creator) {
		this.pendingAreas.put(creator.getName(), creator);
		this.playerAreas.put(sender, creator.getName());
	}

	private AreaCreator loadEditContext(Area area) {
		return area.editArea();
	}

	private AreaCreator saveEditContext(CommandSender sender, AreaCreator area) {
		Area ar = area.createArea();
		AreaCreator retr = loadEditContext(ar);
		putEditContext(sender, retr);
		return retr;
	}

	protected final void addCommand(Action action, Target target,
			BiConsumer<CommandSender, List<String>> function) {
		if (executors.computeIfAbsent(action, k -> new HashMap<>()).put(target, function) != null) {
			assert false : "Already registered: " + action + " " + target;
		}
	}

	@Override
	protected void command(CommandSender sender, Command cmd, String label, String[] args) {
		AreaCreator creator = getEditContext(sender);
		switch (args.length < 1 ? "" : args[0]) {
			case "blocks":
				if (creator == null) {
					sender.sendMessage("You are not in a edit context");
					return;
				}
				switch (args.length < 2 ? "list" : args[1]) {
					case "list":
						break;
					case "add":
						break;
					case "set":
						break;
					case "remove":
						break;
					default:
						sender.sendMessage(translate(BaseTranslation.COMMAND_EDIT_UNKNOWN));
				}
				break;
			case "locations":
				if (creator == null) {
					sender.sendMessage("You are not in a edit context");
					return;
				}
				switch (args.length < 2 ? "list" : args[1]) {
					case "list":
						break;
					case "add":
						break;
					case "set":
						break;
					case "remove":
						break;
					default:
						sender.sendMessage(translate(BaseTranslation.COMMAND_EDIT_UNKNOWN));
				}
				break;
			case "select":
				break;
			case "create":
				break;
			case "delete":
				break;
			case "enable":
				// This enables an area so no edits could be done
				break;
			case "disable":
				// This disables an area so you could work on it
				break;
			case "list":
				sender.sendMessage("The following area's are defined:");
				for (Area area : getCore().getAreas()) {
					sender.sendMessage(" - " + area + ": " + (area.isEnabled() ? "enabled" : "disabled"));
				}
				break;
			case "rename":
				// This renames the selected area
				break;
			case "save":
				// This saves changes to the selected area
				break;
			case "discard":
				// This discards changes to the selected area
				break;
			case "help":
				break;
			default:
				sender.sendMessage(translate(BaseTranslation.COMMAND_EDIT_UNKNOWN));
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
		throw new UnsupportedOperationException("Not supported yet."); //TODO
	}

	protected enum Action {
		ADD("insert", "create"),
		REMOVE("delete"),
		SET("put", "rename"),
		LIST("get"),
		HELP("?");

		private static final Map<String, Action> ACTION_MAP;

		static {
			Map<String, Action> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			for (Action a : values()) {
				for (String alias : a.alliasses) {
					Action existing = map.put(alias, a);
					assert existing == null;
				}
			}
			ACTION_MAP = Collections.unmodifiableMap(map);
		}

		private final List<String> alliasses;

		private Action(String... alliasses) {
			List<String> all = new ArrayList<>();
			all.addAll(Arrays.asList(alliasses));
			all.add(this.name().toLowerCase());
			Collections.sort(all);
			this.alliasses = Collections.unmodifiableList(all);
		}

		public Action getAction(String search) {
			return ACTION_MAP.get(search);
		}
	}

	protected enum Target {
		LOCATION("locations"),
		BOUNDS("bound", "selection"),
		BLOCKS("blocks"),
		MAXPLAYERS("players", "max_players"),
		ALL,
		AREA,
		DESCRIPTION,
		ENABLED("active"),;

		private static final Map<String, Target> TARGET_MAP;

		static {
			Map<String, Target> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
			for (Target a : values()) {
				for (String alias : a.alliasses) {
					Target existing = map.put(alias, a);
					assert existing == null;
				}
			}
			TARGET_MAP = Collections.unmodifiableMap(map);
		}

		private final List<String> alliasses;

		private Target(String... alliasses) {
			List<String> all = new ArrayList<>();
			all.addAll(Arrays.asList(alliasses));
			all.add(this.name().toLowerCase());
			Collections.sort(all);
			this.alliasses = Collections.unmodifiableList(all);
		}

		public Target getTarget(String search) {
			return TARGET_MAP.get(search);
		}
	}
}
