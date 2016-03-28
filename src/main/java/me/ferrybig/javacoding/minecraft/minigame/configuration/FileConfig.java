package me.ferrybig.javacoding.minecraft.minigame.configuration;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.ScheduledFuture;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ferrybig.javacoding.minecraft.minigame.Area;
import me.ferrybig.javacoding.minecraft.minigame.Selection;
import me.ferrybig.javacoding.minecraft.minigame.core.DefaultSelection;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformation;
import me.ferrybig.javacoding.minecraft.minigame.exceptions.ConfigurationException;
import me.ferrybig.javacoding.minecraft.minigame.information.AreaInformationBuilder;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign;
import me.ferrybig.javacoding.minecraft.minigame.status.StatusSign.SignType;
import me.ferrybig.javacoding.minecraft.minigame.translation.SimpleTranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.translation.TranslationMap;
import me.ferrybig.javacoding.minecraft.minigame.translation.Translator;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class FileConfig extends AbstractFullConfig {

	private static final String SECTION_SIGN = "sign";
	private static final String SECTION_AREA = "area";
	private static final String WORLD = "world";
	private static final String LOCATIONS = "locations";
	private static final String BLOCKS = "blocks";
	private static final String MAXPLAYERS = "maxplayers";
	private static final String ENABLED = "enabled";
	private static final String DESCRIPTION = "description";
	private static final String SELECTIONMAXZ = "selection.maxz";
	private static final String SELECTIONMAXY = "selection.maxy";
	private static final String SELECTIONMAXX = "selection.maxx";
	private static final String SELECTIONMINZ = "selection.minz";
	private static final String SELECTIONMINY = "selection.miny";
	private static final String SELECTIONMINX = "selection.minx";
	private final File configFile;
	private final Server server;
	private SoftReference<FileConfiguration> config = new SoftReference<>(null);
	private ScheduledFuture<?> saveTask;

	public FileConfig(EventExecutor executor, File configFile, Server server) {
		super(executor);
		this.configFile = Objects.requireNonNull(configFile, "configFile == null");
		this.server = Objects.requireNonNull(server, "server == null");
	}

	@Override
	public void close() {
		if (saveTask == null) {
			return;
		}
		try {
			FileConfiguration c = config.get();
			if (c != null) {
				saveConfig(c);
			}
		} catch (IOException ex) {
			Logger.getLogger(FileConfig.class.getName()).log(Level.SEVERE, "", ex);
		}
		saveTask.cancel(false);
	}

	@Override
	public Future<?> flushChanges() {
		return executor.submit(() -> {
			if (saveTask == null) {
				return null;
			}
			saveTask.cancel(false);
			saveConfig(config.get());
			return null;
		});
	}

	@Override
	public Future<? extends Translator> loadTranslationMap() {
		return executor.submit(() -> {
			FileConfiguration conf = this.config.get();
			ConfigurationSection translationSection = conf.getConfigurationSection("translations");
			if (translationSection == null) {
				return TranslationMap.getDefaultMappings();
			}
			Map<String, String> translations = new HashMap<>();
			for (String key : translationSection.getKeys(true)) {
				String val = translationSection.getString(key);
				if (val == null) {
					continue;
				}
				translations.put(key, val);
			}
			return new SimpleTranslationMap(translations::get,
					TranslationMap.getDefaultMappings());
		});
	}

	private void scheduleSave() {
		if (saveTask != null) {
			return;
		}
		FileConfiguration conf = config.get();
		if (conf == null) {
			return;
		}
		saveTask = executor.schedule(() -> {
			saveConfig(conf);
			saveTask = null;
			return null;
		}, 5, TimeUnit.MINUTES);
	}

	private void saveConfig(FileConfiguration conf) throws IOException {
		conf.save(configFile);
	}

	private FileConfiguration getConfig() throws ConfigurationException {
		try {
			FileConfiguration conf = this.config.get();
			if (conf != null) {
				return conf;
			}
			conf = new YamlConfiguration();
			if (configFile.exists()) {
				conf.load(configFile);
			}
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
			ConfigurationSection areaSection = conf.getConfigurationSection(SECTION_AREA);
			if (areaSection == null) {
				return areas;
			}
			for (String areaName : areaSection.getKeys(false)) {
				ConfigurationSection section = areaSection.getConfigurationSection(areaName);
				AreaInformationBuilder builder = new AreaInformationBuilder();
				World world = server.getWorld(section.getString(WORLD));
				Selection selection = new DefaultSelection(world);
				selection.getFirstPoint().setX(section.getInt(SELECTIONMINX));
				selection.getFirstPoint().setY(section.getInt(SELECTIONMINY));
				selection.getFirstPoint().setZ(section.getInt(SELECTIONMINZ));
				selection.getSecondPoint().setX(section.getInt(SELECTIONMAXX));
				selection.getSecondPoint().setY(section.getInt(SELECTIONMAXY));
				selection.getSecondPoint().setZ(section.getInt(SELECTIONMAXZ));
				builder.setName(areaName);
				builder.setDescription(section.getString(DESCRIPTION));
				builder.setEnabled(section.getBoolean(ENABLED));
				builder.setMaxPlayers(section.getInt(MAXPLAYERS));
				builder.setBounds(selection);
				{
					Map<String, List<Block>> taggedBlocks = new HashMap<>();
					ConfigurationSection blockSection = section.getConfigurationSection(BLOCKS);
					if (blockSection != null) {
						for (String bockType : blockSection.getKeys(false)) {
							List<Block> blocks = new ArrayList<>();
							for (String val : blockSection.getStringList(bockType)) {
								String[] args = val.split("\\|");
								if (args.length != 3) {
									throw new ConfigurationException("Invalid block format: " + val);
								}
								blocks.add(world.getBlockAt(Integer.parseInt(args[0]),
										Integer.parseInt(args[1]), Integer.parseInt(args[2])));
							}
							if (!blocks.isEmpty()) {
								taggedBlocks.put(bockType, blocks);
							}
						}
					}
					builder.setTaggedBlocks(taggedBlocks);
				}
				{
					Map<String, List<Location>> taggedLocations = new HashMap<>();
					ConfigurationSection locationSection = section.getConfigurationSection(LOCATIONS);
					if (locationSection != null) {
						for (String locType : locationSection.getKeys(false)) {
							List<Location> locations = new ArrayList<>();
							for (String val : locationSection.getStringList(locType)) {
								String[] args = val.split("\\|");
								if (args.length != 5) {
									throw new ConfigurationException("Invalid location format: " + val);
								}
								locations.add(new Location(world, Double.parseDouble(args[0]),
										Double.parseDouble(args[1]), Double.parseDouble(args[2]),
										Float.parseFloat(args[3]), Float.parseFloat(args[4])
								));
							}
							if (!locations.isEmpty()) {
								taggedLocations.put(locType, locations);
							}
						}
					}
					builder.setTaggedLocations(taggedLocations);
				}
				areas.put(areaName, builder.create());
			}
			return areas;
		});
	}

	@Override
	public Future<Map<Block, StatusSign>> loadSigns() {
		return executor.submit(() -> {
			Map<Block, StatusSign> signs = new HashMap<>();
			FileConfiguration conf = getConfig();
			ConfigurationSection signSection = conf.getConfigurationSection(SECTION_SIGN);
			if (signSection == null) {
				return signs;
			}
			for (String key : signSection.getKeys(false)) {
				String[] split = key.split("\\|");
				World w = server.getWorld(split[0]);
				Block b = w.getBlockAt(Integer.parseInt(split[1]), Integer.parseInt(split[2]),
						Integer.parseInt(split[3]));
				SignType type = "RANDOM".equals(signSection.getString(key))
						? SignType.RANDOM : SignType.FIXED_AREA; // TODO more types
				signs.put(b, new StatusSign() {
					@Override
					public SignType getType() {
						return type;
					}
				});
			}
			return signs;
		});
	}

	@Override
	public Future<?> removeArea(String name) {
		return saveArea(name, null);
	}

	@Override
	public Future<?> removeSign(Block b) {
		return executor.submit(() -> {
			FileConfiguration conf = getConfig();
			conf.set(SECTION_SIGN + "." + b.getWorld().getName() + "|" + b.getX()
					+ "|" + b.getY() + "|" + b.getZ(), null);
			scheduleSave();
			return null;
		});
	}

	@Override
	public Future<?> saveArea(String name, Area area) {
		return executor.submit(() -> {
			FileConfiguration conf = getConfig();
			if (area == null) {
				conf.set(SECTION_AREA + "." + name, null);
			} else {
				ConfigurationSection newSection = conf.createSection(SECTION_AREA + "." + name);
				newSection.set(DESCRIPTION, area.getDescription());
				newSection.set(WORLD, area.getBounds().getWorld());
				newSection.set(ENABLED, area.isEnabled());
				newSection.set(MAXPLAYERS, area.maxPlayers());

				newSection.set(SELECTIONMINX, area.getBounds().getFirstPoint().getX());
				newSection.set(SELECTIONMINY, area.getBounds().getFirstPoint().getY());
				newSection.set(SELECTIONMINZ, area.getBounds().getFirstPoint().getZ());

				newSection.set(SELECTIONMAXX, area.getBounds().getSecondPoint().getX());
				newSection.set(SELECTIONMAXY, area.getBounds().getSecondPoint().getY());
				newSection.set(SELECTIONMAXZ, area.getBounds().getSecondPoint().getZ());

				ConfigurationSection block = newSection.createSection(BLOCKS);
				area.getTaggedBlocks().entrySet().stream()
						.filter((blockEntry) -> !(blockEntry.getValue().isEmpty()))
						.forEach((blockEntry) -> {
							List<String> list = new ArrayList<>();
							blockEntry.getValue().stream().forEach((b)
									-> list.add(b.getX() + "|" + b.getY() + "|" + b.getZ()));
							block.set(blockEntry.getKey(), list);
						});
				ConfigurationSection loc = newSection.createSection(BLOCKS);
				area.getTaggedLocations().entrySet().stream()
						.filter((locEntry) -> !(locEntry.getValue().isEmpty()))
						.forEach((locEntry) -> {
							List<String> list = new ArrayList<>();
							locEntry.getValue().stream().forEach((b)
									-> list.add(b.getX() + "|" + b.getY() + "|" + b.getZ()
											+ "|" + b.getPitch() + "|" + b.getYaw()));
							loc.set(locEntry.getKey(), list);
						});

			}
			scheduleSave();
			return null;
		});
	}

	@Override
	public Future<?> saveSign(Block b, StatusSign area) {
		return executor.submit(() -> {
			FileConfiguration conf = getConfig();
			conf.set(SECTION_SIGN + "." + b.getWorld().getName() + "|" + b.getX()
					+ "|" + b.getY() + "|" + b.getZ(), area.getType().name());
			scheduleSave();
			return null;
		});
	}

}
