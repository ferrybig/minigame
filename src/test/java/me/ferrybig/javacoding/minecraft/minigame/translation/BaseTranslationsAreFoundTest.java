package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class BaseTranslationsAreFoundTest {

	private final static TranslationMap map = TranslationMap.getDefaultMappings();

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.stream(BaseTranslation.values()).map(t -> new Object[]{t}).collect(Collectors.toList());
	}

	@Parameterized.Parameter
	public Translation key;

	@Test
	public void keyExistsTest() {
		String returnResult = map.getMessage(key);
		assertNotNull("Could not find translation key " + key, returnResult);
	}
}
