package me.ferrybig.javacoding.minecraft.minigame.verrifier;

import java.util.List;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;

/**
 *
 * @author Fernando
 */
public interface TranslateableAreaVerrifer {
	public List<String> getProblems(AreaInformation area, TranslationMap map);

	public boolean isCorrect(AreaInformation area);

	public Set<String> getValidTeams(AreaInformation area);
	
	public static TranslateableAreaVerrifer wrap(AreaVerifier ar) {
		return new TranslateableAreaVerrifer() {
			@Override
			public List<String> getProblems(AreaInformation area, TranslationMap map) {
				return ar.getProblems(area);
			}

			@Override
			public Set<String> getValidTeams(AreaInformation area) {
				return ar.getValidTeams(area);
			}

			@Override
			public boolean isCorrect(AreaInformation area) {
				return ar.isCorrect(area);
			}
		};
	}
	
	public default AreaVerifier unwrap(TranslationMap map) {
		return new AreaVerifier() {
			@Override
			public List<String> getProblems(AreaInformation area) {
				return TranslateableAreaVerrifer.this.getProblems(area, map);
			}

			@Override
			public Set<String> getValidTeams(AreaInformation area) {
				return TranslateableAreaVerrifer.this.getValidTeams(area);
			}

			@Override
			public boolean isCorrect(AreaInformation area) {
				return TranslateableAreaVerrifer.this.isCorrect(area);
			}
		};
	}
}
