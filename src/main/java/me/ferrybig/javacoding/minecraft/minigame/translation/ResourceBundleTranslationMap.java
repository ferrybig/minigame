package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class ResourceBundleTranslationMap extends TranslationMap {

	private final ResourceBundle bundle;

	public ResourceBundleTranslationMap(TranslationMap parent) {
		this(parent, Locale.US);
	}

	public ResourceBundleTranslationMap(TranslationMap parent, Locale loc) {
		this(parent, getInternalBundle(Objects.requireNonNull(loc)), loc);
	}

	public ResourceBundleTranslationMap(TranslationMap parent, ResourceBundle bundle, Locale loc) {
		super(parent);
		this.bundle = Objects.requireNonNull(bundle, "bundle == null");
		formatter.setLocale(Objects.requireNonNull(loc, "loc == null"));
	}

	@Override
	protected String getMessage(Translation key, Object[] args) {
		String strKey = key.key();
		return bundle.containsKey(strKey) ? replaceArgs(bundle.getString(strKey), args) : null;
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
