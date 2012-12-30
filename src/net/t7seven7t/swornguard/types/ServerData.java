/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.types;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.util.TimeUtil;

/**
 * @author t7seven7t
 */
public class ServerData {
	private final SwornGuard plugin;
	private final long startup;
	
	public ServerData(SwornGuard plugin) {
		this.plugin = plugin;
		this.startup = System.currentTimeMillis();
	}
	
	public long getUptime() {
		return TimeUtil.getTimeDifference(startup, System.currentTimeMillis());
	}
	
	public String getBukkitVersion() {
		return plugin.getServer().getBukkitVersion();
	}
	
	public String getServerName() {
		return plugin.getServer().getServerName();
	}
	
	public int getOnlinePlayerCount() {
		return plugin.getServer().getOnlinePlayers().length;
	}
	
	public int getPlayerCount() {
		return plugin.getPlayerDataCache().getFileListSize();
	}
	
	public int getPlayerCacheSize() {
		return plugin.getPlayerDataCache().getCacheSize();
	}
	
	public int getBannedPlayerCount() {
		return plugin.getServer().getBannedPlayers().size();
	}
	
	public int getIPBanCount() {
		return plugin.getServer().getIPBans().size();
	}

}
