package me.ferrybig.javacoding.minecraft.minigame.information;

import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformationBuilder;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 *
 * @author Fernando
 */
public class AreaInformationBuilderTest {

	private final static Selection MOCK_SELECTION = mock(Selection.class);

	@Test
	public void testSetName() {
		String name = "TEST";
		AreaInformationBuilder instance = createAreaBuilder();
		AreaInformationBuilder result = instance.setName(name);
		assertEquals(name, result.create().getName());
	}

	@Test
	public void testSetEnabled() {
		boolean enabled = false;
		AreaInformationBuilder instance = createAreaBuilder();
		AreaInformationBuilder result = instance.setEnabled(enabled);
		assertEquals(enabled, result.create().isEnabled());
	}

	@Test
	public void testSetDescription() {
		String description = "BlaBlaBla";
		AreaInformationBuilder instance = createAreaBuilder();
		AreaInformationBuilder result = instance.setDescription(description);
		assertEquals(description, result.create().getDescription());
	}

	@Test
	public void testSetTaggedBlocks() {
		Map<String, List<Block>> taggedBlocks
				= Collections.singletonMap("test", Collections.emptyList());
		AreaInformationBuilder instance = createAreaBuilder();
		AreaInformationBuilder result = instance.setTaggedBlocks(taggedBlocks);
		assertEquals(taggedBlocks, result.create().getTaggedBlocks());
	}

	@Test
	public void testSetTaggedLocations() {
		Map<String, List<Location>> taggedLocations
				= Collections.singletonMap("test", Collections.emptyList());
		AreaInformationBuilder instance = createAreaBuilder();
		AreaInformationBuilder result = instance.setTaggedLocations(taggedLocations);
		assertEquals(taggedLocations, result.create().getTaggedLocations());
	}

	@Test
	public void testSetBounds() {
		Selection bounds = mock(Selection.class);
		AreaInformationBuilder instance = createAreaBuilder();
		AreaInformationBuilder result = instance.setBounds(bounds);
		assertEquals(bounds, result.create().getBounds());
	}

	@Test
	public void testSetMaxPlayers() {
		int maxPlayers = 1034;
		AreaInformationBuilder instance = createAreaBuilder();
		AreaInformationBuilder result = instance.setMaxPlayers(maxPlayers);
		assertEquals(maxPlayers, result.create().maxPlayers());
	}

	@Test
	public void testCreateAreaInformation() {
		AreaInformationBuilder instance = new AreaInformationBuilder("Test", MOCK_SELECTION);
		AreaInformation result = instance.create();
		assertEquals("Test", result.getName());
		assertEquals(MOCK_SELECTION, result.getBounds());
	}

	@Test(expected = IllegalStateException.class)
	public void testNullName() {
		AreaInformationBuilder instance = new AreaInformationBuilder(MOCK_SELECTION);
		instance.create();
	}

	@Test(expected = IllegalStateException.class)
	public void testNullBounds() {
		AreaInformationBuilder instance = new AreaInformationBuilder("");
		instance.create();
	}

	protected AreaInformationBuilder createAreaBuilder() {
		return new AreaInformationBuilder("", MOCK_SELECTION);
	}

}
