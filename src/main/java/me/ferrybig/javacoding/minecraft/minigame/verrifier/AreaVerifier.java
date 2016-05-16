package me.ferrybig.javacoding.minecraft.minigame.verrifier;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.information.DefaultResolvedAreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.information.ResolvedAreaInformation;

/**
 * Verifies if a area is correctly build for this minigame
 *
 * @author Fernando
 */
public interface AreaVerifier {

	/**
	 * Get all the problems with an area, or an empty list if the area is correct
	 * @param area The area to check
	 * @return 
	 */
	public List<String> getProblems(AreaInformation area);

	/**
	 * Check if the area is valid
	 * @param area The area to check
	 * @return 
	 */
	public boolean isCorrect(AreaInformation area);

	/**
	 * Gets the valid teams for an area
	 * @param area
	 * @return 
	 */
	public Set<String> getValidTeams(AreaInformation area);

	/**
	 * Validate and resolve an area
	 * @param area
	 * @return 
	 */
	public default ResolvedAreaInformation validate(AreaInformation area) {
		boolean valid = isCorrect(area);
		Set<String> teams;
		if (valid) {
			teams = getValidTeams(area);
		} else {
			teams = Collections.emptySet();
		}
		return new DefaultResolvedAreaInformation(area.getName(), area.isEnabled(),
				area.getDescription(), area.getTaggedBlocks(), area.getTaggedLocations(),
				area.getBounds(), area.maxPlayers(), valid, teams);
	}

}
