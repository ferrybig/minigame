package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translation;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.entity.Player;

public abstract class MessagePhase extends SkippedPhase {

	protected abstract void trigger(PhaseContext area, Collection<Player> players);

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		trigger(area, area.getController().getPlayers().keySet());
		super.onPhaseRegister(area);
	}

	public static final class SimpleMessagePhase extends MessagePhase {

		private final String message;

		public SimpleMessagePhase(String message) {
			this.message = message;
		}

		@Override
		protected void trigger(PhaseContext area, Collection<Player> players) {
			for (Player p : players) {
				p.sendMessage(message);
			}
		}

	}

	public static final class TranslationMessagePhase extends MessagePhase {

		private final Translation message;
		private final Object[] args;

		public TranslationMessagePhase(Translation message) {
			this(message, Translator.CACHED_EMPTY_ARRAY);
		}

		public TranslationMessagePhase(Translation message, Object... args) {
			this.message = message;
			this.args = args;
		}

		@Override
		protected void trigger(PhaseContext area, Collection<Player> players) {
			String rawString = area.translate(message, args);
			for (Player p : players) {
				p.sendMessage(rawString);
			}
		}

	}

	public static final class AdvancedMessagePhase extends MessagePhase {

		private final BiConsumer<PhaseContext, Player> sender;

		public AdvancedMessagePhase(BiConsumer<PhaseContext, Player> sender) {
			this.sender = sender;
		}

		public AdvancedMessagePhase(BiFunction<PhaseContext, Player, String> sender) {
			this((BiConsumer<PhaseContext, Player>) (a, p) -> p.sendMessage(sender.apply(a, p)));
		}

		@Override
		protected void trigger(PhaseContext area, Collection<Player> players) {
			for (Player p : players) {
				sender.accept(area, p);
			}
		}

	}

}
