/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.io;

import java.util.Map;

import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public interface PlayerDataServiceProvider {

	@Deprecated
	public PlayerData getData(final String string);

	public PlayerData getData(final Player player);

	public PlayerData getData(final OfflinePlayer player);

	public Map<String, PlayerData> getAllLoadedPlayerData();

	public Map<String, PlayerData> getAllPlayerData();
}