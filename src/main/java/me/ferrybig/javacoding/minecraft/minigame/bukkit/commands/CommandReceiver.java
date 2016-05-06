
package me.ferrybig.javacoding.minecraft.minigame.bukkit.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface CommandReceiver {

	public List<String> onTabComplete(CommandSender sender, Command cmd,
			List<String> label, List<String> args);

	public void onCommand(CommandSender sender, Command cmd,
			List<String> label, List<String> args);
}
