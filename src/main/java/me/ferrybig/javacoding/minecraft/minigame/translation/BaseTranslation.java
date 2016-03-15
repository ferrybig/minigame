package me.ferrybig.javacoding.minecraft.minigame.translation;

public enum BaseTranslation implements Translation {

	AREA_PLAYER_JOINED("area.player.join"),
	AREA_PLAYER_LEFT("area.player.left"),
	COUNTDOWN_STARTED("countdown.started"),
	COUNTDOWN_SECONDS_LEFT("countdown.left"),
	COUNTDOWN_STARTING("countdown.starting"),
	COUNTDOWN_CANCELLED("countdown.cancelled"),
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
