/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.OfflinePlayer;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.Util;

/**
 * @author t7seven7t
 */
public class PlayerDataCache implements PlayerDataServiceProvider {
	private final SwornGuard plugin;
	private final File folder;
	private final String extension = ".dat";
	private final String folderName = "players";
	private final Object readWriteLock = new Object();
	private final Object mapLock = new Object();
	
	private Map<String, PlayerData> data;
	
	public PlayerDataCache(SwornGuard plugin) {
		this.plugin = plugin;
		this.folder = new File(plugin.getDataFolder(), folderName);
		
		if (!folder.exists())
			folder.mkdir();
		
		this.data = new HashMap<String, PlayerData>();
	}

	public PlayerData getData(final String key) {
		PlayerData value = this.data.get(key);
		if (value == null) {
			File file = new File(folder, getFileName(key));
			if (file.exists()) {
				value = loadData(key);
				addData(key, value);
			}
		}
		
		return value;
	}
	
	public PlayerData getData(final OfflinePlayer player) {
		return getData(player.getName());
	}
	
	public Map<String, PlayerData> getAllLoadedPlayerData() {
		return Collections.unmodifiableMap(data);
	}
	
	public Map<String, PlayerData> getAllPlayerData() {
		Map<String, PlayerData> data = new HashMap<String, PlayerData>();
		data.putAll(this.data);
		for (File file : folder.listFiles())
			if (file.getName().contains(extension)) {
				String fileName = trimFileExtension(file);
				if (!isFileAlreadyLoaded(fileName, data))
					data.put(fileName, loadData(fileName));
			}
		return Collections.unmodifiableMap(data);
	}
	
	private void removeData(final String key) {
		synchronized(mapLock) {
			Map<String, PlayerData> copy = new HashMap<String, PlayerData>();
			copy.putAll(data);
			copy.remove(key);
			data = Collections.unmodifiableMap(copy);
		}
	}
	
	private void addData(final String key, final PlayerData value) {		
		synchronized(mapLock) {
			Map<String, PlayerData> copy = new HashMap<String, PlayerData>();
			copy.putAll(data);
			copy.put(key, value);
			data = Collections.unmodifiableMap(copy);
		}
	}
	
	public PlayerData newData(final String key) {
		PlayerData value = new PlayerData();
		addData(key, value);
		return value;
	}
	
	public PlayerData newData(final OfflinePlayer player) {
		return newData(player.getName());
	}
	
	private void cleanupData() {
		for (String key : getAllLoadedPlayerData().keySet())
			if (!Util.matchOfflinePlayer(key).isOnline())
				removeData(key);
	}
	
	private PlayerData loadData(final String key) {
		synchronized(readWriteLock) {
			return FileSerialization.load(new File(folder, getFileName(key)), PlayerData.class);
		}
	}
	
	public void save() {
		synchronized(readWriteLock) {
			plugin.getLogHandler().log("Saving {0} to disk...", folderName);
			long start = System.currentTimeMillis();
			for (Entry<String, PlayerData> entry : getAllLoadedPlayerData().entrySet())
				synchronized(entry.getValue()) {
					FileSerialization.save(entry.getValue(), new File(folder, getFileName(entry.getKey())));
				}
			cleanupData();
			plugin.getLogHandler().log("{0} saved! [{1}ms]", folderName, System.currentTimeMillis() - start);
		}
	}
	
	private boolean isFileAlreadyLoaded(final String fileName, final Map<String, PlayerData> map) {
		for (String key : map.keySet())
			if (key.equals(fileName))
				return true;
		return false;
	}
	
	private String trimFileExtension(final File file) {
		int index = file.getName().lastIndexOf(extension);
		return index > 0 ? file.getName().substring(0, index) : file.getName(); 
	}
	
	private String getFileName(final String key) {
		return key + extension;
	}
	
	public int getFileListSize() {
		return folder.listFiles().length;
	}
	
	public int getCacheSize() {
		return data.size();
	}
	
}
