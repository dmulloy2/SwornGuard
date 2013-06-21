/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.io;

import java.util.Map;

import org.bukkit.OfflinePlayer;

import net.t7seven7t.swornguard.types.PlayerData;

/**
 * @author t7seven7t
 */
public interface PlayerDataServiceProvider {

	public PlayerData getData(final String key);
	
	public PlayerData getData(final OfflinePlayer player);
	
	public Map<String, PlayerData> getAllLoadedPlayerData();
	
	public Map<String, PlayerData> getAllPlayerData();
	
}
