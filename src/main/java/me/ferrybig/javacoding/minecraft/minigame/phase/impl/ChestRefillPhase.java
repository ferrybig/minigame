package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;

public class ChestRefillPhase extends DelayedPhase {

	private final List<String> targets;
	private final BiConsumer<String, Chest> chestRefil;

	@SafeVarargs
	public ChestRefillPhase(int timeout, TimeUnit unit, BiConsumer<String, Chest> chestRefil,
			Consumer<PhaseContext> afterRefill, String... targets) {
		this(timeout, unit, chestRefil, afterRefill, Arrays.asList(targets));
	}

	public ChestRefillPhase(int timeout, TimeUnit unit, BiConsumer<String, Chest> chestRefil,
			Consumer<PhaseContext> afterRefill, List<String> targets) {
		super(timeout, unit);
		this.chestRefil = Objects.requireNonNull(chestRefil, "chestRefil == null");
		this.targets = Objects.requireNonNull(targets, "targets == null");
		if (targets.isEmpty()) {
			throw new IllegalArgumentException("Empty targets list");
		}
	}

	@Override
	protected void trigger(PhaseContext area) throws Exception {
		for (String target : targets) {
			List<Block> locations = area.getAreaContext().getTaggedBlocks(target);
			if (locations != null) {
				for (Block b : locations) {
					BlockState state = b.getState();
					if (state instanceof Chest) {
						chestRefil.accept(target, (Chest) b);
					}
				}
			}
		}
	}

}
