package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.Objects;

/**
 *
 * @author Fernando
 */
public abstract class TranslationMap {

	private static final TranslationMap DEFAULT_MAPPING;
	private static final TranslationMap FAILURE_MAPPINGS;

	static {
		FAILURE_MAPPINGS = new NullTranslationMap();
		DEFAULT_MAPPING = new ResourceBundleTranslationMap(FAILURE_MAPPINGS);
	}

	public static TranslationMap getDefaultMappings() {
		return DEFAULT_MAPPING;
	}

	public static TranslationMap getFailureMappings() {
		return FAILURE_MAPPINGS;
	}

	private final TranslationMap parent;

	TranslationMap() {
		this.parent = null;
	}

	public TranslationMap(TranslationMap parent) {
		this.parent = Objects.requireNonNull(parent);
	}

	public String get(Translation key) {
		TranslationMap m = this;
		while (m != null) {
			String message = m.getMessage(key);
			if (message != null) {
				return message;
			}
			m = m.parent;
		}
		throw new AssertionError("Should not been reached, failure mappings handle this case");
	}

	protected abstract String getMessage(Translation key);

	public TranslationMap getParent() {
		return parent;
	}

}
