package com.minesworn.swornguard;

import java.util.Map;
import com.minesworn.swornguard.core.io.SCache;
import com.minesworn.swornguard.core.util.Util;

public class PlayerDatabase extends SCache<PlayerInfo> {
		
	public PlayerDatabase() {
		super("players", new PlayerInfoFactory());
	}
	
	public PlayerInfo addPlayer(String name) {
		return addEntity(Util.matchOfflinePlayer(name).getName());
	}
	
	public PlayerInfo getPlayer(String name) {
		return getEntity(Util.matchOfflinePlayer(name).getName());
	}
	
	public void removePlayer(String name) {
		removeEntity(Util.matchOfflinePlayer(name).getName());
	}
	
	@Override
	public void cleanupEntities() {
		for (String name : getEntities().keySet()) {
			if (!Util.matchOfflinePlayer(name).isOnline()) {
				removePlayer(name);
			}
		}
	}
	
	public Map<String, PlayerInfo> getAllPlayerInfo() {
		return this.getAllEntities();
	}
	
	public Map<String, PlayerInfo> getPlayers() {
		return getEntities();
	}
	
	public int getPlayerDatabaseSize() {
		return this.getDatabaseSize();
	}
	
}
