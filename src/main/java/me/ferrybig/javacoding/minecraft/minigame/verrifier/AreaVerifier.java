
package me.ferrybig.javacoding.minecraft.minigame.verrifier;

import java.util.List;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;

/**
 * Verifies if a area is correctly build for this minigame
 * @author Fernando
 */
public interface AreaVerifier {

	/**
	 * Checks the area for missing information, returns an empty list if no errors were found
	 * @param area
	 * @param translations
	 * @return 
	 */
	public List<String> verifyInformation(AreaInformation area, TranslationMap translations);
	
	/**
	 * Analize the area for valid teams
	 * @param area
	 * @return 
	 */
	public List<String> getValidTeams(AreaInformation area);
	
}
