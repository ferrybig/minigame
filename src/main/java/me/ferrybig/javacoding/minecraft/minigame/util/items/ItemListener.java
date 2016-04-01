package me.ferrybig.javacoding.minecraft.minigame.util.items;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@FunctionalInterface
public interface ItemListener {

	public static final ItemListener CLOSING_LISTENER = (p, s) -> p.closeInventory();

	public void itemClicked(Player player, ItemStack stack);

	public default void close(Player player) {
	}

}
