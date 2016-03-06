
package me.ferrybig.javacoding.minecraft.minigame.verrifier;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.DefaultResolvedAreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.ResolvedAreaInformation;

/**
 * Verifies if a area is correctly build for this minigame
 * @author Fernando
 */
public interface AreaVerifier {

	public List<String> getProblems(AreaInformation area);

	public boolean isCorrect(AreaInformation area);

	public Set<String> getValidTeams(AreaInformation area);
	
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
