package me.ferrybig.javacoding.minecraft.minigame.translation;

public enum BaseTranslation implements Translation {

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
