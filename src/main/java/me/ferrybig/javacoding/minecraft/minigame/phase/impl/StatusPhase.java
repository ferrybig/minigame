package me.ferrybig.javacoding.minecraft.minigame.phase.impl;

import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import me.ferrybig.javacoding.minecraft.minigame.context.AreaContext;
import me.ferrybig.javacoding.minecraft.minigame.context.PhaseContext;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.translation.BaseTranslation;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translation;

public class StatusPhase extends SkippedPhase {

	private static final AttributeKey<State> CURRENT_STATE
			= AttributeKey.valueOf(StatusPhase.class, "state");
	private static final AttributeKey<List<BiConsumer<AreaContext, State>>> LISTENERS
			= AttributeKey.valueOf(StatusPhase.class, "listeners");
	private final State onLoadState;
	private final State onResetState;

	public StatusPhase(State onLoadState, State onResetState) {
		this.onLoadState = onLoadState;
		this.onResetState = onResetState;
	}

	@Override
	public void afterReset(PhaseContext area) {
		if (onResetState != null) {
			setState(area.getAreaContext(), onResetState);
		}
		super.afterReset(area);
	}

	@Override
	public void onPhaseRegister(PhaseContext area) throws Exception {
		if (onLoadState != null) {
			setState(area.getAreaContext(), onLoadState);
		}
		super.onPhaseRegister(area);
	}

	public static State getState(AreaContext area) {
		Objects.requireNonNull(area, "area == null");
		State state = area.attr(CURRENT_STATE).get();
		if (state == null) {
			return State.JOINABLE;
		}
		return state;
	}

	public static void setState(AreaContext area, State state) {
		Objects.requireNonNull(area, "area == null");
		Attribute<State> attr = area.attr(CURRENT_STATE);
		Attribute<List<BiConsumer<AreaContext, State>>> listeners = area.attr(LISTENERS);
		if (Objects.equals(attr.get(), state)) {
			return;
		}
		attr.set(state);
		List<BiConsumer<AreaContext, State>> listen = listeners.get();
		if (listen == null) {
			return;
		}
		listen.forEach(l -> l.accept(area, state));
	}

	public static void registerForStateUpdates(AreaContext area,
			BiConsumer<AreaContext, State> listener, boolean callDirect) {
		Objects.requireNonNull(area, "area == null");
		Objects.requireNonNull(listener, "listener == null");
		Attribute<List<BiConsumer<AreaContext, State>>> listeners = area.attr(LISTENERS);
		listeners.setIfAbsent(new CopyOnWriteArrayList<>());
		listeners.get().add(listener);
		if (callDirect) {
			listener.accept(area, getState(area));
		}
	}

	@Override
	public void onPlayerJoin(PhaseContext area, PlayerJoinMessage player) throws Exception {
		if(getState(area.getAreaContext()) != State.PLAYING) {
			player.setCancelled(true);
		}
		super.onPlayerJoin(area, player);
	}

	@Override
	public void onPlayerPreJoin(PhaseContext area, PlayerPreJoinMessage player) throws Exception {
		if(getState(area.getAreaContext()) != State.PLAYING) {
			player.setCancelled(true);
		}
		super.onPlayerPreJoin(area, player);
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
