package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.text.MessageFormat;
import java.util.Objects;

/**
 *
 * @author Fernando
 */
public abstract class TranslationMap implements Translator {

	private static final TranslationMap DEFAULT_MAPPING;
	private static final TranslationMap FAILURE_MAPPINGS;
	protected final MessageFormat formatter = new MessageFormat("");
	protected final TranslationMap parent;

	static {
		FAILURE_MAPPINGS = new NullTranslationMap();
		DEFAULT_MAPPING = new ResourceBundleTranslationMap(FAILURE_MAPPINGS);
	}

	TranslationMap() {
		this.parent = null;
	}

	public TranslationMap(TranslationMap parent) {
		this.parent = Objects.requireNonNull(parent);
	}

	@Override
	public final String translate(Translation translation, Object... args) {
		TranslationMap m = this;
		while (m != null) {
			String message = m.getMessage(translation, args);
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

	protected String replaceArgs(String bundleResult, Object[] args) {
		if (args.length == 0 || bundleResult == null) {
			return bundleResult;
		}
		formatter.applyPattern(bundleResult);
		return formatter.format(args);
	}

	public static TranslationMap getDefaultMappings() {
		return DEFAULT_MAPPING;
	}

	public static TranslationMap getFailureMappings() {
		return FAILURE_MAPPINGS;
	}

}
