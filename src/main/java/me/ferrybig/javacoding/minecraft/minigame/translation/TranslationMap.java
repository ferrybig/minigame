package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.Objects;

/**
 *
 * @author Fernando
 */
public abstract class TranslationMap {

	private static final Object[] CACHED_EMPTY_ARRAY;
	private static final TranslationMap DEFAULT_MAPPING;
	private static final TranslationMap FAILURE_MAPPINGS;

	static {
		FAILURE_MAPPINGS = new NullTranslationMap();
		DEFAULT_MAPPING = new ResourceBundleTranslationMap(FAILURE_MAPPINGS);
		CACHED_EMPTY_ARRAY = new Object[0];
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
		return get(key, CACHED_EMPTY_ARRAY);
	}

	public String get(Translation key, Object... args) {
		TranslationMap m = this;
		while (m != null) {
			String message = m.getMessage(key, args);
			if (message != null) {
				return message;
			}
			m = m.parent;
		}
		throw new AssertionError("Should not been reached, failure mappings handle this case");
	}

	protected abstract String getMessage(Translation key, Object[] arguments);

	public TranslationMap getParent() {
		return parent;
	}

}
