/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.PlayerInfo;
import net.t7seven7t.util.LogHandler;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import com.google.common.io.Files;

/**
 * @author t7seven7t
 */
@SuppressWarnings("deprecation")
public class PlayerDataConverter {

	public static void run(SwornGuard plugin) {
		LogHandler logger = plugin.getLogHandler();
		logger.log("Starting player data conversion...");
		long start = System.currentTimeMillis();
		
		File folder = new File(plugin.getDataFolder(), "players");
		
		for (File file : folder.listFiles()) {
			PlayerInfo oldData = new PlayerInfo();
			SPersist.load(plugin, oldData, PlayerInfo.class, file);
						
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("firstLogin", oldData.getFirstOnlineTime());
			data.put("lastOnline", oldData.getLastOnlineTime());
			
			data.put("ipAddressList", oldData.getIpAddressList());
			data.put("profilerList", oldData.getProfilerList());
			
			data.put("logins", oldData.getLoginCount());
			data.put("blocksDeleted", oldData.getBlockDeleteCount());
			data.put("blocksBuilt", oldData.getBlockBuildCount());
			data.put("messages", oldData.getMessageCount());
			data.put("animalKills", oldData.getAnimalKills());
			data.put("monsterKills", oldData.getMobKills());
			data.put("playerKills", oldData.getPlayerKills());
			data.put("deaths", oldData.getDeaths());
			data.put("kicks", oldData.getKickCount());
			data.put("jails", oldData.getJailCount());
			data.put("bans", oldData.getBanCount());
			data.put("playersKicked", oldData.getNumPlayersBanned());
			data.put("playersBanned", oldData.getNumPlayersBanned());
			data.put("reportsRespondedTo", oldData.getNumCheatReportsRespondedTo());
			data.put("patrols", oldData.getNumTimesPatrolled());
			data.put("stoneMined", oldData.getStoneMined());
			data.put("diamondMined", oldData.getDiamondMined());
			data.put("ironMined", oldData.getIronMined());
			data.put("factions", oldData.getFactionCount());
			
			data.put("lastKick", oldData.getLastKickTime());
			data.put("lastJail", oldData.getLastJailTime());
			data.put("lastBan", oldData.getLastBanTime());
			data.put("lastUnban", oldData.getLastUnbanTime());
			data.put("onlineTime", oldData.getTotalTimeSpentOnServer());
		
			data.put("lastKicker", oldData.getLastKickedBy());
			data.put("lastBanner", oldData.getLastBannedBy());
			data.put("lastJailer", oldData.getLastJailedBy());
			data.put("lastUnbanner", oldData.getLastUnbannedBy());
			data.put("lastKickReason", oldData.getLastKickReason());
			data.put("lastBanReason", oldData.getLastBanReason());
			data.put("lastJailReason", oldData.getLastJailReason());
			data.put("lastFaction", oldData.getMostRecentFaction());
			
			PlayerData newData = (PlayerData) ConfigurationSerialization.deserializeObject(data, PlayerData.class);
			FileSerialization.save(newData, new File(folder, file.getName() + ".dat"));
			file.delete();
		}
		
		logger.log("Old player data converted! Took {0}ms", System.currentTimeMillis() - start);
	}
	
	public static void fixShit(SwornGuard plugin) {
		LogHandler logger = plugin.getLogHandler();
		logger.log("Fixing shit...");
		long start = System.currentTimeMillis();
		
		File folder = new File(plugin.getDataFolder(), "players");
		
		for (File file : folder.listFiles()) {
			try {
				Files.copy(file, new File(folder, file.getName() + ".dat"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			file.delete();
		}
		
		logger.log("Shit fixed {0}ms", System.currentTimeMillis() - start);
	}
	
}
