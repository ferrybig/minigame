
package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.ConfigurationException;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileConfig extends AbstractConfig implements SignConfig, AreaConfig {

	private SoftReference<FileConfiguration> config;
	
	private final File configFile;

	public FileConfig(EventExecutor executor, File configFile) {
		super(executor);
		this.configFile = Objects.requireNonNull(configFile);
	}

	@Override
	public void close() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Future<?> flushChanges() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	private FileConfiguration getConfig() throws ConfigurationException {
		try {
			FileConfiguration conf = this.config.get();
			if(conf != null) {
				return conf;
			}
			conf = new YamlConfiguration();
			if(configFile.exists())
				conf.load(configFile);
			this.config = new SoftReference<>(conf);
			return conf;
		} catch (IOException | InvalidConfigurationException ex) {
			throw new ConfigurationException("Unable to read config file", ex);
		}
	}

	@Override
	public Future<Map<String, AreaInformation>> loadAreas() {
		return executor.submit(() -> {
			Map<String, AreaInformation> areas = new HashMap<>();
			FileConfiguration conf = getConfig();
			ConfigurationSection areaSection = conf.getConfigurationSection("area");
			if(areaSection == null)
				return areas;
			for(String areaName : areaSection.getKeys(false)) {
				ConfigurationSection section = areaSection.getConfigurationSection(areaName);
				
			}
			return areas;
		});
	}

	@Override
	public Future<Map<Block, StatusSign>> loadSigns() {
		throw new UnsupportedOperationException("Not supported yet.");
	}


	@Override
	public Future<?> removeArea(String name) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Future<?> removeSign(Block location) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Future<?> saveArea(String name, Area area) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Future<?> saveSign(Block location, StatusSign area) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	

}
