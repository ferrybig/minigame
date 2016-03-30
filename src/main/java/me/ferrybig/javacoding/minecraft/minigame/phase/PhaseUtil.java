package me.ferrybig.javacoding.minecraft.minigame.phase;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.phase.impl.MessagePhase;
import me.ferrybig.javacoding.minecraft.minigame.phase.impl.StatusPhase;
import me.ferrybig.javacoding.minecraft.minigame.phase.impl.StatusPhase.State;
import me.ferrybig.javacoding.minecraft.minigame.phase.impl.WaitingPhase;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translation;
import org.bukkit.entity.Player;

public class PhaseUtil {

	private PhaseUtil() {
		assert false : "Cannot construct";
	}

	public static Phase waiting(long time, TimeUnit unit) {
		return new WaitingPhase(time, unit);
	}

	public static Phase state(State state) {
		return new StatusPhase(state, null);
	}

	public static MessagePhase simpleMessage(String message) {
		return new MessagePhase.SimpleMessagePhase(message);
	}

	public static MessagePhase translatedMessage(Translation message, Object... args) {
		return new MessagePhase.TranslationMessagePhase(message, args);
	}

	public static MessagePhase advancedMessageFactory(BiFunction<PhaseContext, Player, String> factory) {
		return new MessagePhase.AdvancedMessagePhase(factory);
	}

	public static MessagePhase advancedMessageSender(BiConsumer<PhaseContext, Player> factory) {
		return new MessagePhase.AdvancedMessagePhase(factory);
	}
}
