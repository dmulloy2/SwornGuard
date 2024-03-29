package net.t7seven7t.swornguard.io;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import net.dmulloy2.io.FileSerialization;
import net.dmulloy2.io.IOUtil;
import net.dmulloy2.io.UUIDFetcher;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

/**
 * @author dmulloy2
 */

public class PlayerDataCache implements PlayerDataServiceProvider {
	private final File folder;
	private final String extension = ".dat";
	private final String folderName = "players";
	private final SwornGuard plugin;

	private final ConcurrentMap<String, PlayerData> cache;

	public PlayerDataCache(SwornGuard plugin) {
		this.folder = new File(plugin.getDataFolder(), folderName);
		if (! folder.exists())
			folder.mkdirs();

		this.cache = new ConcurrentHashMap<String, PlayerData>(64, 0.75F, 64);
		this.plugin = plugin;
		this.convertToUUID();
	}

	// ---- Data Getters

	private final PlayerData getData(String key) {
		// Check cache first
		PlayerData data = cache.get(key);
		if (data == null) {
			// Attempt to load it
			File file = new File(folder, getFileName(key));
			if (file.exists()) {
				data = loadData(key);
				if (data == null) {
					// Corrupt data :(
					if (! file.renameTo(new File(folder, file.getName() + "_bad")))
						file.delete();
					return null;
				}

				// Cache it
				cache.put(key, data);
			}
		}

		return data;
	}

	@Override
	public final PlayerData getData(UUID uniqueId) {
		return getData(uniqueId.toString());
	}

	@Override
	public final PlayerData getData(Player player) {
		PlayerData data = getData(getKey(player));

		// Online players should always have data
		if (data == null) {
			data = newData(player);
		}

		// Try to fetch history from Essentials
		List<String> history = data.getHistory();
		if (history == null && plugin.isEssentialsEnabled()) {
			history = plugin.getEssentialsHandler().getHistory(player.getUniqueId());
		}

		// Account for name changes
		String lastKnownBy = data.getLastKnownBy();
		if (lastKnownBy != null && ! lastKnownBy.isEmpty()) {
			if (! lastKnownBy.equals(player.getName())) {
				if (history == null) {
					history = new ArrayList<String>();
				}

				// Ensure we have the right casing
				if (lastKnownBy.equalsIgnoreCase(player.getName())) {
					plugin.getLogHandler().log("Corrected casing for {0}''s name.", player.getName());

					history.remove(lastKnownBy);
					data.setLastKnownBy(lastKnownBy = player.getName());
					history.add(lastKnownBy);
				} else {
					// Name change!
					plugin.getLogHandler().log("{0} changed their name to {1}.", lastKnownBy, player.getName());

					data.setLastKnownBy(lastKnownBy = player.getName());
					history.add(lastKnownBy);
				}
			}
		} else {
			data.setLastKnownBy(lastKnownBy = player.getName());
		}

		if (history == null) {
			history = new ArrayList<String>();
			history.add(player.getName());
		}

		data.setHistory(history);
		data.setUniqueId(player.getUniqueId().toString());

		// Return
		return data;
	}

	@Override
	public final PlayerData getData(OfflinePlayer player) {
		// Slightly different handling for Players
		if (player.isOnline())
			return getData(player.getPlayer());

		// Attempt to get by name
		return getData(getKey(player));
	}

	// ---- Data Management

	public final PlayerData newData(String key) {
		// Construct
		PlayerData data = new PlayerData();

		// Cache and return
		cache.put(key, data);
		return data;
	}

	public final PlayerData newData(Player player) {
		return newData(getKey(player));
	}

	public final PlayerData newData(OfflinePlayer player) {
		return newData(getKey(player));
	}

	private final PlayerData loadData(String key) {
		File file = new File(folder, getFileName(key));

		try {
			PlayerData data = FileSerialization.load(file, PlayerData.class);
			data.setUniqueId(key);
			return data;
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading data for {0}", key));
			return null;
		}
	}

	public final void save() {
		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Saving players to disk...");

		for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet()) {
			try {
				File file = new File(folder, getFileName(entry.getKey()));
				FileSerialization.save(entry.getValue(), file);
			} catch (Throwable ex) {
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "saving data for {0}", entry.getKey()));
			}
		}

		plugin.getLogHandler().log("Players saved. Took {0} ms.", System.currentTimeMillis() - start);
	}

	// Legacy

	@Deprecated
	public final void save(boolean cleanup) {
		save();
		if (cleanup)
			cleanupData();
	}

	public final void cleanupData() {
		// Get all online players into an array list
		List<String> online = new ArrayList<String>();
		for (Player player : Util.getOnlinePlayers())
			online.add(player.getName());

		// Actually cleanup the data
		for (String key : getAllLoadedPlayerData().keySet())
			if (! online.contains(key))
				cache.remove(key);

		// Clear references
		online.clear();
		online = null;
	}

	// ---- Mass Getters

	@Override
	public final Map<String, PlayerData> getAllLoadedPlayerData() {
		return Collections.unmodifiableMap(cache);
	}

	@Override
	public final Map<String, PlayerData> getAllPlayerData() {
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();
		data.putAll(cache);

		File[] files = folder.listFiles();
		if (files == null || files.length == 0) {
			return Collections.unmodifiableMap(data);
		}

		for (File file : files) {
			if (file.getName().contains(extension)) {
				String fileName = IOUtil.trimFileExtension(file, extension);
				if (! isFileLoaded(fileName))
					data.put(fileName, loadData(fileName));
			}
		}

		return Collections.unmodifiableMap(data);
	}

	// ---- UUID Conversion

	private final void convertToUUID() {
		File updated = new File(folder, ".updated");
		if (updated.exists()) {
			return;
		}

		long start = System.currentTimeMillis();
		plugin.getLogHandler().log("Checking for unconverted files");

		Map<String, PlayerData> data = getUnconvertedData();
		if (data.isEmpty()) {
			try {
				updated.createNewFile();
			} catch (Throwable ex) { }
			return;
		}

		plugin.getLogHandler().log("Converting {0} files!", data.size());

		try {
			List<String> names = new ArrayList<String>(data.keySet());
			ImmutableList.Builder<List<String>> builder = ImmutableList.builder();
			int namesCopied = 0;
			while (namesCopied < names.size()) {
				builder.add(ImmutableList.copyOf(names.subList(namesCopied, Math.min(namesCopied + 100, names.size()))));
				namesCopied += 100;
			}

			List<UUIDFetcher> fetchers = new ArrayList<UUIDFetcher>();
			for (List<String> namesList : builder.build()) {
				fetchers.add(new UUIDFetcher(namesList));
			}

			ExecutorService e = Executors.newFixedThreadPool(3);
			List<Future<Map<String, UUID>>> results = e.invokeAll(fetchers);

			File archive = new File(folder.getParentFile(), "archive");
			if (! archive.exists())
				archive.mkdir();

			for (Future<Map<String, UUID>> result : results) {
				Map<String, UUID> uuids = result.get();
				for (Entry<String, UUID> entry : uuids.entrySet()) {
					try {
						// Get and update
						String name = entry.getKey();
						String uniqueId = entry.getValue().toString();
						PlayerData dat = data.get(name);
						dat.setLastKnownBy(name);

						// Archive the old file
						File file = new File(folder, getFileName(name));
						Files.move(file, new File(archive, file.getName()));

						// Create and save new file
						File newFile = new File(folder, getFileName(uniqueId));
						FileSerialization.save(dat, newFile);
					} catch (Throwable ex) {
						plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "converting {0}", entry.getKey()));
					}
				}
			}
		} catch (Throwable ex) {
			plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "converting to UUID-based lookups!"));
			return;
		}

		plugin.getLogHandler().log("Successfully converted to UUID-based lookups! Took {0} ms!", System.currentTimeMillis() - start);
	}

	private final Map<String, PlayerData> getUnconvertedData() {
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();

		File[] files = folder.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				String name = file.getName();
				return name.contains(extension) && name.length() != 40;
			}

		});

		for (File file : files) {
			try {
				PlayerData loaded = FileSerialization.load(file, PlayerData.class);
				if (loaded != null) {
					String fileName = IOUtil.trimFileExtension(file, extension);
					loaded.setLastKnownBy(fileName);
					data.put(fileName, loaded);
				}
			} catch (Throwable ex) {
				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "loading data {0}", file));
			}
		}

		return Collections.unmodifiableMap(data);
	}

	// ---- Util

	private final String getKey(OfflinePlayer player) {
		return player.getUniqueId().toString();
	}

	private final String getFileName(String key) {
		return key + extension;
	}

	private final boolean isFileLoaded(String fileName) {
		return cache.keySet().contains(fileName);
	}

	public int getFileListSize() {
		return folder.listFiles().length;
	}

	public int getCacheSize() {
		return cache.size();
	}
}