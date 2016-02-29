package me.ferrybig.javacoding.minecraft.minigame.translation;

public enum BaseTranslation implements Translation {

	AREA_PLAYER_JOINED("area.player.join"),
	AREA_PLAYER_LEFT("area.player.left"),
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
