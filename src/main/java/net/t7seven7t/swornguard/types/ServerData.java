/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.types;

import java.lang.management.ManagementFactory;

import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;

/**
 * @author t7seven7t
 */
public class ServerData {
	private final SwornGuard plugin;

	public ServerData(SwornGuard plugin) {
		this.plugin = plugin;
	}
	
	public long getUptime() {
		return TimeUtil.getTimeDifference(ManagementFactory.getRuntimeMXBean().getStartTime(), System.currentTimeMillis());
	}
	
	public String getBukkitVersion() {
		return plugin.getServer().getBukkitVersion();
	}
	
	public String getServerName() {
		return plugin.getServer().getServerName();
	}
	
	public int getOnlinePlayerCount() {
		return Util.getOnlinePlayers().size();
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
