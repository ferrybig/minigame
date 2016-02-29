package me.ferrybig.javacoding.minecraft.minigame.translation;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class ResourceBundleTranslationMap extends TranslationMap {

	private final ResourceBundle bundle;
	private static final LoadingCache<Translation, String> KEY_LOOKUP
			= CacheBuilder.from(System.getProperty("minigameAPI.translationSpec", ""))
			.build(new CacheLoader<Translation, String>() {
				@Override
				public String load(Translation key) throws Exception {
					return key.name().toLowerCase(Locale.US).replace('_', '.').intern();
				}

			});

	public ResourceBundleTranslationMap(TranslationMap parent) {
		this(parent, Locale.US);
	}

	public ResourceBundleTranslationMap(TranslationMap parent, Locale loc) {
		this(parent, getInternalBundle(loc));
	}

	public ResourceBundleTranslationMap(TranslationMap parent, ResourceBundle bundle) {
		super(parent);
		this.bundle = Objects.requireNonNull(bundle);
	}

	@Override
	protected String getMessage(Translation key) {
		String strKey;
		try {
			strKey = KEY_LOOKUP.get(key);
		} catch (ExecutionException ex) {
			throw new IllegalArgumentException("Translation key incorrect?", ex);
		}
		return bundle.containsKey(strKey) ? bundle.getString(strKey) : null;
	}

	private static ResourceBundle getInternalBundle(Locale loc) {
		return ResourceBundle.getBundle(
				ResourceBundleTranslationMap.class.getName().replace('.', '/'),
				loc);
	}

}
