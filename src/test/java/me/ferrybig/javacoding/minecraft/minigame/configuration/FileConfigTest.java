package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import static me.ferrybig.javacoding.minecraft.minigame.executors.MockExecutor.getExecutor;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Fernando
 */
public class FileConfigTest {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	public final EventExecutor executor = getExecutor();

	@Mock
	public Server server;

	@Mock
	public World world;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		when(server.getWorld("world")).thenReturn(world);
		when(world.getBlockAt(anyInt(), anyInt(), anyInt())).then(i -> {
			Block bl = mock(Block.class);
			when(bl.getX()).thenReturn(i.getArgumentAt(0, Integer.class));
			when(bl.getY()).thenReturn(i.getArgumentAt(1, Integer.class));
			when(bl.getZ()).thenReturn(i.getArgumentAt(2, Integer.class));
			when(bl.getWorld()).thenReturn(world);
			return bl;
		});
		verifyNoMoreInteractions(world, server);
	}

	@Test
	public void canLoadEmptyFile() throws Exception {
		File f = folder.newFile("config.yml");
		f.delete();
		Files.copy(getClass().getResourceAsStream("FileConfig_empty.yml"), f.toPath());

		FileConfig conf = new FileConfig(executor, f, server);

		// TODO: Test the following seperate
		Translator translator = conf.loadTranslationMap().get();
		Map<String, AreaInformation> area = conf.loadAreas().get();
		Map<Block, StatusSign> signs = conf.loadSigns().get();

		assertNotNull(translator);
		assertNotNull(area);
		assertNotNull(signs);

		assertTrue(area.isEmpty());
		assertTrue(signs.isEmpty());
	}

	@Test
	public void canLoadAreasFile() throws Exception {
		File f = folder.newFile("config.yml");
		f.delete();
		Files.copy(getClass().getResourceAsStream("FileConfig_area.yml"), f.toPath());

		FileConfig conf = new FileConfig(executor, f, server);
		Map<String, AreaInformation> area = conf.loadAreas().get();

		assertNotNull(area);
		assertEquals(1, area.size());
		assertTrue(area.containsKey("testArea"));
		{
			AreaInformation ar = area.get("testArea");
			assertNotNull(ar);
			assertEquals("testArea", ar.getName());
			assertEquals("Hello World!", ar.getDescription());
			assertTrue(ar.isEnabled());
			assertEquals(13, ar.maxPlayers());
			assertNotNull(ar.getBounds());
			assertEquals(10, ar.getBounds().getFirstPoint().getBlockX());
			assertEquals(11, ar.getBounds().getFirstPoint().getBlockY());
			assertEquals(12, ar.getBounds().getFirstPoint().getBlockZ());
			assertEquals(20, ar.getBounds().getSecondPoint().getBlockX());
			assertEquals(21, ar.getBounds().getSecondPoint().getBlockY());
			assertEquals(22, ar.getBounds().getSecondPoint().getBlockZ());
			assertEquals(world, ar.getBounds().getWorld());
		}
	}

	@Test
	public void canLoadAreasFileAdvancedTest() throws Exception {
		File f = folder.newFile("config.yml");
		f.delete();
		Files.copy(getClass().getResourceAsStream("FileConfig_area_advanced.yml"), f.toPath());

		FileConfig conf = new FileConfig(executor, f, server);
		Map<String, AreaInformation> area = conf.loadAreas().get();

		assertNotNull(area);
		assertEquals(2, area.size());
		assertTrue(area.containsKey("testArea1"));
		{
			AreaInformation ar = area.get("testArea1");
			assertNotNull(ar);
			assertEquals("testArea1", ar.getName());
			assertEquals("Hello", ar.getDescription());
			assertTrue(ar.isEnabled());
			assertEquals(13, ar.maxPlayers());
			{
				Selection bounds = ar.getBounds();
				assertNotNull(bounds);
				assertEquals(10, bounds.getFirstPoint().getBlockX());
				assertEquals(11, bounds.getFirstPoint().getBlockY());
				assertEquals(12, bounds.getFirstPoint().getBlockZ());
				assertEquals(20, bounds.getSecondPoint().getBlockX());
				assertEquals(21, bounds.getSecondPoint().getBlockY());
				assertEquals(22, bounds.getSecondPoint().getBlockZ());
				assertEquals(world, bounds.getWorld());
			}
		}
		assertTrue(area.containsKey("testArea2"));
		{
			AreaInformation ar = area.get("testArea2");
			assertNotNull(ar);
			assertEquals("testArea2", ar.getName());
			assertEquals("Hello World", ar.getDescription());
			assertFalse(ar.isEnabled());
			assertEquals(113, ar.maxPlayers());
			{
				Selection bounds = ar.getBounds();
				assertNotNull(bounds);
				{
					Vector firstPoint = bounds.getFirstPoint();
					assertNotNull(firstPoint);
					assertEquals(110, firstPoint.getBlockX());
					assertEquals(111, firstPoint.getBlockY());
					assertEquals(112, firstPoint.getBlockZ());
				}
				{
					Vector secondPoint = bounds.getSecondPoint();
					assertNotNull(secondPoint);
					assertEquals(120, secondPoint.getBlockX());
					assertEquals(121, secondPoint.getBlockY());
					assertEquals(122, secondPoint.getBlockZ());
				}
				assertEquals(world, bounds.getWorld());
			}
			{
				Map<String, List<Location>> taggedLocations = ar.getTaggedLocations();
				assertNotNull(taggedLocations);
				assertEquals(2, taggedLocations.size());
				{
					List<Location> locations = taggedLocations.get("red");
					assertNotNull(locations);
					assertEquals(2, locations.size());
					assertSame(locations, ar.getTaggedLocations("red"));
					{
						Location loc = locations.get(0);
						assertNotNull(loc);
						assertEquals(130.5, loc.getX(), 0.001);
						assertEquals(140.5, loc.getY(), 0.001);
						assertEquals(150.5, loc.getZ(), 0.001);
						assertEquals(0, loc.getYaw(), 0.001);
						assertEquals(90, loc.getPitch(), 0.001);
						assertSame(world, loc.getWorld());
					}
					{
						Location loc = locations.get(1);
						assertNotNull(loc);
						assertEquals(140.5, loc.getX(), 0.001);
						assertEquals(130.5, loc.getY(), 0.001);
						assertEquals(150.5, loc.getZ(), 0.001);
						assertEquals(0, loc.getYaw(), 0.001);
						assertEquals(90, loc.getPitch(), 0.001);
						assertSame(world, loc.getWorld());
					}
				}
				{
					List<Location> locations = taggedLocations.get("blue");
					assertNotNull(locations);
					assertEquals(2, locations.size());
					assertSame(locations, ar.getTaggedLocations("blue"));
					{
						Location loc = locations.get(0);
						assertNotNull(loc);
						assertEquals(130.5, loc.getX(), 0.001);
						assertEquals(140.5, loc.getY(), 0.001);
						assertEquals(130.5, loc.getZ(), 0.001);
						assertEquals(180, loc.getYaw(), 0.001);
						assertEquals(90, loc.getPitch(), 0.001);
						assertSame(world, loc.getWorld());
					}
					{
						Location loc = locations.get(1);
						assertNotNull(loc);
						assertEquals(140.5, loc.getX(), 0.001);
						assertEquals(130.5, loc.getY(), 0.001);
						assertEquals(130.5, loc.getZ(), 0.001);
						assertEquals(180, loc.getYaw(), 0.001);
						assertEquals(90, loc.getPitch(), 0.001);
						assertSame(world, loc.getWorld());
					}
				}
			}
			{
				Map<String, List<Block>> taggedBlocks = ar.getTaggedBlocks();
				assertNotNull(taggedBlocks);
				assertEquals(2, taggedBlocks.size());
				{
					List<Block> blocks = taggedBlocks.get("red");
					assertNotNull(blocks);
					assertEquals(2, blocks.size());
					assertSame(blocks, ar.getTaggedBlocks("red"));
					{
						Block bl = blocks.get(0);
						assertNotNull(blocks.toString(), bl);
						assertEquals(130, bl.getX());
						assertEquals(140, bl.getY());
						assertEquals(150, bl.getZ());
						assertSame(world, bl.getWorld());
					}
					{
						Block bl = blocks.get(1);
						assertNotNull(blocks.toString(), bl);
						assertEquals(140, bl.getX());
						assertEquals(130, bl.getY());
						assertEquals(150, bl.getZ());
						assertSame(world, bl.getWorld());
					}
				}
				{
					List<Block> blocks = taggedBlocks.get("blue");
					assertNotNull(blocks);
					assertEquals(2, blocks.size());
					assertSame(blocks, ar.getTaggedBlocks("blue"));
					{
						Block bl = blocks.get(0);
						assertNotNull(blocks.toString(), bl);
						assertEquals(130, bl.getX());
						assertEquals(140, bl.getY());
						assertEquals(130, bl.getZ());
						assertSame(world, bl.getWorld());
					}
					{
						Block bl = blocks.get(1);
						assertNotNull(blocks.toString(), bl);
						assertEquals(140, bl.getX());
						assertEquals(130, bl.getY());
						assertEquals(130, bl.getZ());
						assertSame(world, bl.getWorld());
					}
				}
			}
		}
	}

}
