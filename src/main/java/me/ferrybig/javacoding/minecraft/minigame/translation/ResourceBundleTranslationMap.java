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
		String strKey = key.key();
		return bundle.containsKey(strKey) ? bundle.getString(strKey) : null;
	}

	private static ResourceBundle getInternalBundle(Locale loc) {
		return ResourceBundle.getBundle(calculateBaseFileName(), loc);
	}
	
	private static String calculateBaseFileName() {
		String base = ResourceBundleTranslationMap.class.getName().replace('.', '/');
		int index = base.lastIndexOf('/');
		return base.substring(0, index + 1) + "base";
	}

}
