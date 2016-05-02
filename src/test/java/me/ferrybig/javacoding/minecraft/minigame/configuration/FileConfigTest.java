package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import static me.ferrybig.javacoding.minecraft.minigame.executors.MockExecutor.getExecutor;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
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
		AreaInformation ar = area.get("testArea");
		assertNotNull(ar);
		assertEquals("testArea", ar.getName());
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
