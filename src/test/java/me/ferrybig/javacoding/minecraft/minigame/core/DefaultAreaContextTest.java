package me.ferrybig.javacoding.minecraft.minigame.core;

import java.util.function.Function;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaCreator;
import me.ferrybig.javacoding.minecraft.minigame.verrifier.AreaVerifier;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Fernando
 */
public class DefaultAreaContextTest {

	@Mock
	public Function<? super AreaCreator, Area> constructor;
	@Mock
	public AreaVerifier verifier;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void creatorCallSuperFunctionOnBuild() {
		DefaultAreaCreator crea = new DefaultAreaCreator(constructor, verifier);
		crea.setName("test");
		crea.createArea();
		verify(constructor).apply(crea);
	}

}
