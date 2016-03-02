
package me.ferrybig.javacoding.minecraft.minigame.verrifier;

import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.util.SafeUtil;

public class ExceptionCatchingVerrifier implements AreaVerifier {
	
	private static final Logger LOG = Logger.getLogger(ExceptionCatchingVerrifier.class.getName());
	private final AreaVerifier upstream;

	public ExceptionCatchingVerrifier(AreaVerifier upstream) {
		this.upstream = upstream;
	}

	@Override
	public List<String> getValidTeams(AreaInformation area) {
		try {
			List<String> teams = upstream.getValidTeams(area);
			if(teams == null) {
				LOG.log(Level.SEVERE, "{0}.getValidTeams() returned null!", SafeUtil.toString(upstream));
				return Collections.emptyList();
			}
			return teams;
		} catch(Throwable a) {
			LOG.log(Level.SEVERE, "Problem calling " + SafeUtil.toString(upstream) + ".getValidTeams()", a);
			return Collections.emptyList();
		}
	}

	@Override
	public List<String> verifyInformation(AreaInformation area, TranslationMap translations) {
		try {
			List<String> errors = upstream.verifyInformation(area, translations);
			if(errors == null) {
				LOG.log(Level.SEVERE, "{0}.verifyInformation() returned null!", SafeUtil.toString(upstream));
				return Collections.emptyList();
			}
			return errors;
		} catch(Throwable a) {
			String name = "Problem calling " + SafeUtil.toString(upstream) + ".verifyInformation()";
			LOG.log(Level.SEVERE, name, a);
			return Collections.singletonList(name);
		}
	}

}
