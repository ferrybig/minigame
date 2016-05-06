
package me.ferrybig.javacoding.minecraft.minigame.bukkit.commands;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public abstract class AbstractCommandReceiver implements TabExecutor, CommandReceiver{

	protected String makeLabel(CommandSender sender, List<String> label) {
		return label.stream().collect(Collectors.joining(" ",
				sender instanceof Player ? "/" : "", ""));
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.testPermission(sender)) {
			List<String> l = new LinkedList<>();
			l.add(label);
			this.onCommand(sender, command, l, Arrays.asList(args));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

	}

}
