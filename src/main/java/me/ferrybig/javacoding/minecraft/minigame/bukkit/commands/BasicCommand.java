package me.ferrybig.javacoding.minecraft.minigame.bukkit.commands;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import me.ferrybig.javacoding.minecraft.minigame.GameCore;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translation;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public abstract class BasicCommand implements TabExecutor, Translator {

	private final Supplier<GameCore> core;
	private final BooleanSupplier coreEnabled;
	private GameCore loadedCore = null;

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
		if (loadedCore != null) {
			return loadedCore;
		}
		if (!hasCore()) {
			throw new NoSuchElementException();
		}
		GameCore c = this.core.get();
		this.loadedCore = c;
		coreLoaded(c);
		c.terminationFuture().addListener(new CloseListener(this));
		return c;
	}

	protected void coreLoaded(GameCore core) {
	}

	protected void coreLost(GameCore core) {
	}

	@Override
	public abstract List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args);

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!cmd.testPermission(sender)) {
			return true;
		}
		if (!hasCore(sender)) {
			return true;
		}
		command(sender, cmd, label, args);
		return true;
	}

	public abstract void command(CommandSender sender, Command cmd, String label, String[] args);

	@Override
	public String translate(Translation translation, Object... args) {
		return getCore().getInfo().getTranslations().translate(translation, args);
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
				c.coreLost(c.loadedCore);
				c.loadedCore = null;
			}
		}

	}

}
