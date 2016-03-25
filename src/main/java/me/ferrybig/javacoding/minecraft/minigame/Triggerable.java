package me.ferrybig.javacoding.minecraft.minigame;

import java.util.concurrent.Callable;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreJoinMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerPreLeaveMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerSpectateMessage;
import me.ferrybig.javacoding.minecraft.minigame.messages.PlayerTeamMessage;
import me.ferrybig.javacoding.minecraft.minigame.util.ExceptionRunnable;

/**
 * Represents a context that is triggerable.
 *
 * @author Fernando
 */
public interface Triggerable {

	void triggerExceptionCaucht(Throwable exception);

	void triggerNextPhase();

	void triggerPlayerChangeTeam(PlayerTeamMessage player);

	void triggerPlayerJoin(PlayerJoinMessage player);

	void triggerPlayerLeave(PlayerLeaveMessage player);

	void triggerPlayerPreJoin(PlayerPreJoinMessage player);

	void triggerPlayerPreLeave(PlayerPreLeaveMessage player);

	void triggerPlayerSpectate(PlayerSpectateMessage player);

	void triggerReset();

	void triggerUserEvent(Object event);

	default <T> T safeCall(Callable<T> call) {
		return safeCall(call, call);
	}

	default void safeCall(ExceptionRunnable call) {
		safeCall(call, call);
	}

	<T> T safeCall(Callable<T> call, Object origin);

	default void safeCall(ExceptionRunnable call, Object origin) {
		safeCall(() -> {
			call.run();
			return null;
		}, origin);
	}

}
