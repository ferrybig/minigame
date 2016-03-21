package me.ferrybig.javacoding.minecraft.minigame.bukkit.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author Fernando
 */
public class EditAreaCommand extends BasicCommand {

	private final Map<String, AreaCreator> pendingAreas = new HashMap<>();
	private final Map<Action, Map<Target, BiConsumer<CommandSender, List<String>>>> executors = new HashMap<>();

	;

	public EditAreaCommand(Supplier<GameCore> core, BooleanSupplier coreEnabled) {
		super(core, coreEnabled);
	}

	protected final void addCommand(Action action, Target target,
			BiConsumer<CommandSender, List<String>> function) {
		executors.computeIfAbsent(action, k -> new HashMap<>()).put(target, function);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.testPermission(sender)) {
			return true;
		}
		String subcommand;
		if (args.length == 0) {
			subcommand = "";
		} else {
			subcommand = args[0];
		}
		switch (subcommand) {

		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
		throw new UnsupportedOperationException("Not supported yet."); //TODO
	}

	protected enum Action {
		ADD("insert"),
		REMOVE("delete"),
		SET("put"),
		LIST("get"),;

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
		NAME,
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
