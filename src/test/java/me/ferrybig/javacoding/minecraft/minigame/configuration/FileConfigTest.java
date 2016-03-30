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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author Fernando
 */
public class FileConfigTest {

	@Rule
	public final TemporaryFolder folder = new TemporaryFolder();

	public final EventExecutor executor = getExecutor();

	@Test
	public void canLoadEmptyFile() throws Exception {
		File f = folder.newFile("config.yml");
		f.delete();
		Files.copy(getClass().getResourceAsStream("FileConfig_empty.yml"), f.toPath());
		Server server = mock(Server.class);

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
		Server server = mock(Server.class);
		World world = mock(World.class);
		when(server.getWorld("world")).thenReturn(world);

		FileConfig conf = new FileConfig(executor, f, server);
		Map<String, AreaInformation> area = conf.loadAreas().get();

		assertNotNull(area);
		
		assertEquals(1, area.size());

		assertTrue(area.containsKey("testArea"));


	}

}
