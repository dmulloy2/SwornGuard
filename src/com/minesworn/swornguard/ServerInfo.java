package com.minesworn.swornguard;

import org.bukkit.Bukkit;

import com.minesworn.swornguard.core.util.Util;

public class ServerInfo {
	
	transient long startup;
	
	public String getUptime() {
		return Util.formatTimeDifference(startup, System.currentTimeMillis());
	}
	
	public String getBukkitVersion() {
		return Bukkit.getBukkitVersion();
	}
	
	public String getName() {
		return Bukkit.getServerName();
	}
	
	public int getOnlinePlayerCount() {
		return Bukkit.getOnlinePlayers().length;
	}
	
	public int getPlayerCount() {
		return SwornGuard.playerdatabase.getPlayerDatabaseSize();
	}
	
	public int getPlayersLoadedNowCount() {
		return SwornGuard.playerdatabase.getPlayers().size();
	}
	
	public int getBanCount() {
		return Bukkit.getBannedPlayers().size();
	}
	
	public int getIPBanCount() {
		return Bukkit.getIPBans().size();
	}
	
	public ServerInfo() {
		this.startup = System.currentTimeMillis();
	}

}
