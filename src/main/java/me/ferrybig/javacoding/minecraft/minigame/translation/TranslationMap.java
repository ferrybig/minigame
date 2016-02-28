package me.ferrybig.javacoding.minecraft.minigame.translation;

/**
 *
 * @author Fernando
 */
public abstract class TranslationMap {

	private final TranslationMap parent;
	
	public TranslationMap(TranslationMap parent) {
		this.parent = parent;
	}
	
	public String get(Translation key) {
		TranslationMap m = this;
		while(m != null) {
			String message = m.getMessage(key);
			if(message != null) {
				return message;
			}
			m = m.parent;
		}
		return key.toString();
	}
	
	protected abstract String getMessage(Translation key);
			
}
