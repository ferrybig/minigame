
package me.ferrybig.javacoding.minecraft.minigame.verrifier;

import java.util.List;
import java.util.Set;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;

/**
 * Verifies if a area is correctly build for this minigame
 * @author Fernando
 */
public interface AreaVerifier {

	public List<String> getProblems(AreaInformation area);

	public boolean isCorrect(AreaInformation area);

	public Set<String> getValidTeams(AreaInformation area);

}
