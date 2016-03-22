package me.ferrybig.javacoding.minecraft.minigame.translation;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class BaseTranslationsAreFoundTest {

	private final static TranslationMap MAP = TranslationMap.getDefaultMappings();
	private final static Set<String> SEEN_KEYS = new HashSet<>();

	@BeforeClass
	public static void beforeClass() {
		SEEN_KEYS.clear();
	}

	@Parameterized.Parameters
	public static Collection<Object[]> data() {
		return Arrays.stream(BaseTranslation.values()).map(t -> new Object[]{t}).collect(Collectors.toList());
	}

	@Parameterized.Parameter
	public Translation key;

	@Test
	public void keyExistsTest() {
		String returnResult = MAP.getMessage(key, new Object[0]);
		assertNotNull("Could not find translation key " + key, returnResult);
	}

	@Test
	public void keyAlreadyDefinedTest() {
		assertTrue("Key already seen: " + key.key(), SEEN_KEYS.add(key.key()));
	}
}
