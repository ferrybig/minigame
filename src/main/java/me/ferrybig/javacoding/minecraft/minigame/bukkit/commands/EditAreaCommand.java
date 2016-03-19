package me.ferrybig.javacoding.minecraft.minigame.bukkit.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 *
 * @author Fernando
 */
public class EditAreaCommand implements TabExecutor {
	
	private final Map<String, AreaCreator> pendingAreas = new HashMap<>();

	@Override
	public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] strings) {
		throw new UnsupportedOperationException("Not supported yet."); //TODO
	}

	@Override
	public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] strings) {
		throw new UnsupportedOperationException("Not supported yet."); //TODO
	}
	
}
