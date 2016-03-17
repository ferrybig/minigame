package me.ferrybig.javacoding.minecraft.minigame.translation;

public enum BaseTranslation implements Translation {

	AREA_PLAYER_JOINED("area.player.join"),
	AREA_PLAYER_LEFT("area.player.left"),
	COUNTDOWN_STARTED("countdown.started"),
	COUNTDOWN_SECONDS_LEFT("countdown.left"),
	COUNTDOWN_STARTING("countdown.starting"),
	COUNTDOWN_CANCELLED("countdown.cancelled"),
	SIGNS_FOOTER("signs.footer"),
	SIGNS_HEADER("signs.header"),
	SIGNS_PLAYERCOUNTER("signs.playercounter"),
	SIGNS_STATE_LOADING("signs.state.loading"),
	SIGNS_STATE_JOINABLE("signs.state.joinable"),
	SIGNS_STATE_PROGRESS("signs.state.in_progress"),
	SIGNS_STATE_ENDING("signs.state.ending"),
	SIGNS_STATE_ERROR("signs.state.error"),
	SIGNS_STATE_SHUTDOWN("signs.state.shutdown"),
	SIGNS_INTERACT_JOIN("signs.interact.join"),
	SIGNS_INTERACT_FULL("signs.interact.full"),
	SIGNS_INTERACT_STARTED("signs.interact.started"),
	;

	private final String key;

	private BaseTranslation(String key) {
		this.key = key;
	}

	@Override
	public String key() {
		return key;
	}

}
