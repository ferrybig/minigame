
package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.util.items.ItemListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ItemMenuPhase extends SkippedPhase implements Listener {

	private final Map<Player, ItemEntry> openedInventories = new HashMap<>();
	private static final AttributeKey<ItemMenuPhase> REGISTERED
			= AttributeKey.valueOf(ItemMenuPhase.class, "registered");

	private final Map<Integer, ItemEntry> items;
	private final boolean replaceExisting;
	private boolean unregister;
	private PhaseContext registered;

	private ItemMenuPhase(Map<Integer, ItemEntry> items, boolean replaceExisting) {
		this.items = items;
		this.replaceExisting = replaceExisting;
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		this.registered = area;
		Attribute<ItemMenuPhase> attr = area.getAreaContext().attr(REGISTERED);
		ItemMenuPhase old = attr.get();
		if(old != null)
			old.cleanup();
		area.registerNativeListener(this);
		attr.set(this);
		area.getController().getPlayers().keySet().iterator().forEachRemaining(this::giveItems);
		super.onPhaseRegister(area);
	}

	private void giveItems(Player player) {
		Inventory inv = player.getInventory();
		for(Entry<Integer, ItemEntry> item : items.entrySet()) {
			inv.setItem(item.getKey(), item.getValue().getStack());
		}
	}

	@Override
	public void onPhaseUnregister(PhaseContext area) throws Exception {
		close(area.getAreaContext());
		super.onPhaseUnregister(area);
	}

	private boolean checkRegistered() {
		if(unregister) {
			cleanup();
			return false;
		}
		return true;
	}

	private void cleanup() {
		Map<Player, ItemEntry> opened = this.openedInventories;
		if(opened != null && !opened.isEmpty()) {
			for(Entry<Player, ItemEntry> entry : opened.entrySet()) {
				entry.getValue().getListener().close(entry.getKey());
			}
		}
		registered.getAreaContext().attr(REGISTERED).remove();
	}
	
	public static void close(AreaContext area) {
		Attribute<ItemMenuPhase> attr = area.attr(REGISTERED);
		ItemMenuPhase phase = attr.getAndRemove();
		if(phase != null) {
			phase.unregister = true;
		}
	}

	@EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEvent(InventoryClickEvent evt) {
		if(!(evt.getWhoClicked() instanceof Player))
			return;
		Player player = (Player) evt.getWhoClicked();
		if(!registered.getAreaContext().isInArea(player)) {
			return;
		}
		if(!checkRegistered()) {
			return;
		}
		evt.setCancelled(true);
		ItemEntry newItem = this.items.get(evt.getRawSlot());
		if(newItem == null)
			return;
		ItemEntry open = this.openedInventories.get(player);
		if(open != null) {
			open.getListener().close(player);
		}
		this.openedInventories.put(player, newItem);
		newItem.getListener().itemClicked(player, newItem.getStack());

	}
	
	public static class CloseItemMenuPhase extends SkippedPhase {
		
		@Override
		public void onPhaseRegister(PhaseContext area) throws Exception {
			close(area.getAreaContext());
			super.onPhaseRegister(area);
		}
		
	}
	
	private final class ItemEntry {
		private final ItemStack stack;
		private final ItemListener listener;

		public ItemEntry(ItemStack stack, ItemListener listener) {
			this.stack = Objects.requireNonNull(stack, "stack == null");
			this.listener = Objects.requireNonNull(listener, "listener == null");
		}

		public ItemStack getStack() {
			return stack;
		}

		public ItemListener getListener() {
			return listener;
		}

	}
	
}
