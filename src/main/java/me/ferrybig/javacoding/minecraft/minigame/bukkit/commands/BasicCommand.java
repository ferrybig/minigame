package me.ferrybig.javacoding.minecraft.minigame.bukkit.commands;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public abstract class BasicCommand implements TabExecutor {

	private final Supplier<GameCore> core;
	private final BooleanSupplier coreEnabled;
	private boolean hasCore = false;

	public BasicCommand(Supplier<Optional<GameCore>> core) {
		this(() -> core.get().get(), () -> core.get().isPresent());
	}

	public BasicCommand(Supplier<GameCore> core, BooleanSupplier coreEnabled) {
		this.core = core;
		this.coreEnabled = coreEnabled;
	}

	public boolean hasCore() {
		if (!coreEnabled.getAsBoolean()) {
			return false;
		}
		return true;
	}

	public boolean hasCore(CommandSender sender) {
		if (!hasCore()) {
			sender.sendMessage("Plugin not fully loaded...");
			return false;
		}
		return true;
	}

	public GameCore getCore() {
		if (!hasCore()) {
			throw new NoSuchElementException();
		}
		GameCore c = this.core.get();
		if (!hasCore) {
			hasCore = true;
			c.terminationFuture().addListener(new CloseListener(this));
		}
		return c;
	}

	private static class CloseListener implements GenericFutureListener<Future<Object>> {

		private final Reference<BasicCommand> cmd;

		public CloseListener(BasicCommand cmd) {
			this.cmd = new WeakReference<>(cmd);
		}

		@Override
		public void operationComplete(Future<Object> future) throws Exception {
			BasicCommand c = this.cmd.get();
			if (c != null) {
				c.hasCore = false;
			}
		}

	}

}
