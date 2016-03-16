package me.ferrybig.javacoding.minecraft.minigame.phase;

import java.util.function.BiConsumer;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import static me.ferrybig.javacoding.minecraft.minigame.phase.StatusPhase.State.JOINABLE;
import me.ferrybig.javacoding.minecraft.minigame.translation.BaseTranslation;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translation;

public class StatusPhase {

	public static State getState(AreaContext area) {
		return JOINABLE;
	}

	public static void registerForStateUpdates(AreaContext area,
			BiConsumer<AreaContext, State> listener, boolean callDirect) {

	}

	public enum State implements me.ferrybig.javacoding.minecraft.minigame.translation.Translation {
		// TODO make netty enum
		JOINABLE(BaseTranslation.SIGNS_STATE_JOINABLE),
		PLAYING(BaseTranslation.SIGNS_STATE_PROGRESS),
		ENDING(BaseTranslation.SIGNS_STATE_ENDING),;
		private final String key;

		private State(Translation key) {
			this(key.key());
		}

		private State(String key) {
			this.key = key;
		}

		@Override
		public String key() {
			return key;
		}

	}
}
