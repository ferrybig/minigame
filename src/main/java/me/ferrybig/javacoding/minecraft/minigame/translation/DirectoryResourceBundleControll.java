package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

public class DirectoryResourceBundleControll extends ResourceBundle.Control {

	private final File directory;

	public DirectoryResourceBundleControll(File directory) {
		this.directory = directory;
	}

	@Override
	public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
		ResourceBundle bundle = null;
		if (format.equals("java.properties")) {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, format);
			File bundleFile = new File(directory, resourceName);
			if (bundleFile.exists()) {
				try (BufferedInputStream bis = new BufferedInputStream(
						new FileInputStream(bundleFile))) {
					bundle = new PropertyResourceBundle(bis);
				}
			}
		}
		return bundle;
	}

	@Override
	public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader, ResourceBundle bundle, long loadTime) {
		if (format.equals("java.properties")) {
			String bundleName = toBundleName(baseName, locale);
			String resourceName = toResourceName(bundleName, format);
			File bundleFile = new File(directory, resourceName);
			if (bundleFile.lastModified() > loadTime) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<String> getFormats(String baseName) {
		return Collections.singletonList("java.properties");
	}

}
