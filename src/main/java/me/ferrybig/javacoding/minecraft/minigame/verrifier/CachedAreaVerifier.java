
package me.ferrybig.javacoding.minecraft.minigame.verrifier;

import java.util.List;
import java.util.Objects;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;

public class CachedAreaVerifier implements AreaVerifier {
	
	private final TranslationMap translations;
	private final AreaVerifier upstream;

	public CachedAreaVerifier(TranslationMap translations, AreaVerifier upstream) {
		this.translations = Objects.requireNonNull(translations);
		this.upstream = Objects.requireNonNull(upstream);
	}

	@Override
	public List<String> getValidTeams(AreaInformation area) {
		return upstream.getValidTeams(area);
	}

	@Override
	public List<String> verifyInformation(AreaInformation area, TranslationMap translations) {
		return upstream.verifyInformation(area, translations);
	}
	
	public List<String> verifyInformation(AreaInformation area) {
		return verifyInformation(area, translations);
	}

}
