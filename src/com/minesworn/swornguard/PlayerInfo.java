package com.minesworn.swornguard;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

import com.minesworn.swornguard.core.io.Entity;

public class PlayerInfo extends Entity {
	
	long firstOnlineTime;
	long lastOnlineTime;
	
	String ipAddress = new String();
	ArrayList<String> ipAddressList = new ArrayList<String>();
	ArrayList<String> profilerList = new ArrayList<String>();
	
	int loginCount;
	int blockDeleteCount;
	int blockBuildCount;
	int messageCount;
	
	transient long lastMobKill;
	transient long lastPlayerKill;
	transient long lastAnimalKill;
	transient boolean onlineNow;
	transient long lastWarnedForFlying;
	transient long lastWarnedForXray;
	transient long lastSpamReport;
	@Deprecated
	transient HashMap<Long, org.bukkit.entity.Entity> recentAttacks = new HashMap<Long, org.bukkit.entity.Entity>();
	transient long lastAttacked;
	
	transient long lastUpdatedTimeSpent;
	transient long lastFlyTick;
	
	transient boolean autoPatrolling = false;
	transient boolean cheaterInspecting = false;
	transient long stoppedAutoPatrolling;
	transient Location locationBeforePatrolling;
	
	transient boolean vanished = false;
	
	int animalKills;
	int mobKills;
	int playerKills;
	int deaths;
	
	int kickCount;
	long lastKickTime;
	String lastKickedBy = new String();
	String lastKickReason = new String();
		
	int jailCount;
	long lastJailTime;
	String lastJailedBy = new String();
	String lastJailReason = new String();
	
	int banCount;
	long lastBanTime;
	String lastBannedBy = new String();
	String lastBanReason = new String();
	
	long lastUnbanTime;
	String lastUnbannedBy = new String();
	
	int numPlayersBanned;
	int numPlayersKicked;
	int numCheatReportsRespondedTo;
	int numTimesPatrolled;
	long totalTimeSpentOnServer;
	
	int factionCount;
	String mostRecentFaction = new String();
	
	int stoneMined;
	int diamondMined;
	int ironMined;
	
	public long getLastOnlineTime() {
		return lastOnlineTime;
	}
	public void setLastOnlineTime(long lastOnlineTime) {
		this.lastOnlineTime = lastOnlineTime;
	}
	public boolean isOnlineNow() {
		return onlineNow;
	}
	public void setOnlineNow(boolean onlineNow) {
		this.onlineNow = onlineNow;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getLoginCount() {
		return loginCount;
	}
	public void setLoginCount(int loginCount) {
		this.loginCount = loginCount;
	}
	public int getBlockDeleteCount() {
		return blockDeleteCount;
	}
	public void setBlockDeleteCount(int blockDeleteCount) {
		this.blockDeleteCount = blockDeleteCount;
	}
	public int getBlockBuildCount() {
		return blockBuildCount;
	}
	public void setBlockBuildCount(int blockBuildCount) {
		this.blockBuildCount = blockBuildCount;
	}
	public int getMessageCount() {
		return messageCount;
	}
	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	public int getKickCount() {
		return kickCount;
	}
	public void setKickCount(int kickCount) {
		this.kickCount = kickCount;
	}
	public long getLastKickTime() {
		return lastKickTime;
	}
	public void setLastKickTime(long lastKickTime) {
		this.lastKickTime = lastKickTime;
	}
	public String getLastKickedBy() {
		return lastKickedBy;
	}
	public void setLastKickedBy(String lastKickedBy) {
		this.lastKickedBy = lastKickedBy;
	}
	public String getLastKickReason() {
		return lastKickReason;
	}
	public void setLastKickReason(String lastKickReason) {
		this.lastKickReason = lastKickReason;
	}
	public int getJailCount() {
		return jailCount;
	}
	public void setJailCount(int jailCount) {
		this.jailCount = jailCount;
	}
	public long getLastJailTime() {
		return lastJailTime;
	}
	public void setLastJailTime(long lastJailTime) {
		this.lastJailTime = lastJailTime;
	}
	public String getLastJailedBy() {
		return lastJailedBy;
	}
	public void setLastJailedBy(String lastJailedBy) {
		this.lastJailedBy = lastJailedBy;
	}
	public String getLastJailReason() {
		return lastJailReason;
	}
	public void setLastJailReason(String lastJailReason) {
		this.lastJailReason = lastJailReason;
	}
	public int getBanCount() {
		return banCount;
	}
	public void setBanCount(int banCount) {
		this.banCount = banCount;
	}
	public long getLastBanTime() {
		return lastBanTime;
	}
	public void setLastBanTime(long lastBanTime) {
		this.lastBanTime = lastBanTime;
	}
	public String getLastBannedBy() {
		return lastBannedBy;
	}
	public void setLastBannedBy(String lastBannedBy) {
		this.lastBannedBy = lastBannedBy;
	}
	public String getLastBanReason() {
		return lastBanReason;
	}
	public void setLastBanReason(String lastBanReason) {
		this.lastBanReason = lastBanReason;
	}
	public long getLastUnbanTime() {
		return lastUnbanTime;
	}
	public void setLastUnbanTime(long lastUnbanTime) {
		this.lastUnbanTime = lastUnbanTime;
	}
	public String getLastUnbannedBy() {
		return lastUnbannedBy;
	}
	public void setLastUnbannedBy(String lastUnbannedBy) {
		this.lastUnbannedBy = lastUnbannedBy;
	}
	public int getNumPlayersBanned() {
		return numPlayersBanned;
	}
	public void setNumPlayersBanned(int numPlayersBanned) {
		this.numPlayersBanned = numPlayersBanned;
	}
	public int getNumPlayersKicked() {
		return numPlayersKicked;
	}
	public void setNumPlayersKicked(int numPlayersKicked) {
		this.numPlayersKicked = numPlayersKicked;
	}
	public long getTotalTimeSpentOnServer() {
		return totalTimeSpentOnServer;
	}
	public void setTotalTimeSpentOnServer(long totalTimeSpentOnServer) {
		this.totalTimeSpentOnServer = totalTimeSpentOnServer;
	}
	public int getFactionCount() {
		return factionCount;
	}
	public void setFactionCount(int factionCount) {
		this.factionCount = factionCount;
	}
	public String getMostRecentFaction() {
		return mostRecentFaction;
	}
	public void setMostRecentFaction(String mostRecentFaction) {
		this.mostRecentFaction = mostRecentFaction;
	}
	public long getFirstOnlineTime() {
		return firstOnlineTime;
	}
	public void setFirstOnlineTime(long firstOnlineTime) {
		this.firstOnlineTime = firstOnlineTime;
	}
	public ArrayList<String> getIpAddressList() {
		return ipAddressList;
	}
	public long getLastWarnedForFlying() {
		return lastWarnedForFlying;
	}
	public void setLastWarnedForFlying(long lastWarnedForFlying) {
		this.lastWarnedForFlying = lastWarnedForFlying;
	}
	public int getMobKills() {
		return mobKills;
	}
	public void setMobKills(int mobKills) {
		this.mobKills = mobKills;
	}
	public int getPlayerKills() {
		return playerKills;
	}
	public void setPlayerKills(int playerKills) {
		this.playerKills = playerKills;
	}
	public long getLastMobKill() {
		return lastMobKill;
	}
	public void setLastMobKill(long lastMobKill) {
		this.lastMobKill = lastMobKill;
	}
	public long getLastPlayerKill() {
		return lastPlayerKill;
	}
	public void setLastPlayerKill(long lastPlayerKill) {
		this.lastPlayerKill = lastPlayerKill;
	}
	public int getAnimalKills() {
		return animalKills;
	}
	public void setAnimalKills(int animalKills) {
		this.animalKills = animalKills;
	}
	public long getLastAnimalKill() {
		return lastAnimalKill;
	}
	public void setLastAnimalKill(long lastAnimalKill) {
		this.lastAnimalKill = lastAnimalKill;
	}
	public long getLastSpamReport() {
		return lastSpamReport;
	}
	public void setLastSpamReport(long lastSpamReport) {
		this.lastSpamReport = lastSpamReport;
	}
	public ArrayList<String> getProfilerList() {
		return profilerList;
	}
	
	public void setLastKick(String by, String reason, long time) {
		kickCount++;
		lastKickedBy = by;
		lastKickReason = reason;
		lastKickTime = time;
	}
	public int getStoneMined() {
		return stoneMined;
	}
	public void setStoneMined(int stoneMined) {
		this.stoneMined = stoneMined;
	}
	public int getDiamondMined() {
		return diamondMined;
	}
	public void setDiamondMined(int diamondMined) {
		this.diamondMined = diamondMined;
	}
	public int getIronMined() {
		return ironMined;
	}
	public void setIronMined(int ironMined) {
		this.ironMined = ironMined;
	}
	public HashMap<Long, org.bukkit.entity.Entity> getRecentAttacks() {
		return recentAttacks;
	}
	public long getLastAttacked() {
		return lastAttacked;
	}
	public void setLastAttacked(long lastAttacked) {
		this.lastAttacked = lastAttacked;
	}
	
	public void updateSpentTime() {
		long now = System.currentTimeMillis();
		if (lastUpdatedTimeSpent > lastOnlineTime) {
			this.totalTimeSpentOnServer = this.totalTimeSpentOnServer + (now - lastUpdatedTimeSpent);
		} else
			this.totalTimeSpentOnServer = this.totalTimeSpentOnServer + (now - lastOnlineTime);
		lastUpdatedTimeSpent = now;
	}
	public long getLastWarnedForXray() {
		return lastWarnedForXray;
	}
	public void setLastWarnedForXray(long lastWarnedForXray) {
		this.lastWarnedForXray = lastWarnedForXray;
	}
	public long getFlyTicks() {
		return lastFlyTick;
	}
	public void setFlyTicks(long flyTicks) {
		this.lastFlyTick = flyTicks;
	}
	public int getDeaths() {
		return deaths;
	}
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	public boolean isAutoPatrolling() {
		return autoPatrolling;
	}
	public void setAutoPatrolling(boolean autoPatrolling) {
		this.autoPatrolling = autoPatrolling;
	}
	public boolean isVanished() {
		return vanished;
	}
	public void setVanished(boolean vanished) {
		this.vanished = vanished;
	}
	public long getStoppedAutoPatrolling() {
		return stoppedAutoPatrolling;
	}
	public void setStoppedAutoPatrolling(long stoppedAutoPatrolling) {
		this.stoppedAutoPatrolling = stoppedAutoPatrolling;
	}
	public Location getLocationBeforePatrolling() {
		return locationBeforePatrolling;
	}
	public void setLocationBeforePatrolling(Location locationBeforePatrolling) {
		this.locationBeforePatrolling = locationBeforePatrolling;
	}
	public boolean isCheaterInspecting() {
		return cheaterInspecting;
	}
	public void setCheaterInspecting(boolean cheaterInspecting) {
		this.cheaterInspecting = cheaterInspecting;
	}
	
	public int getNumCheatReportsRespondedTo() {
		return numCheatReportsRespondedTo;
	}
	public void setNumCheatReportsRespondedTo(int numCheatReportsRespondedTo) {
		this.numCheatReportsRespondedTo = numCheatReportsRespondedTo;
	}
	public int getNumTimesPatrolled() {
		return numTimesPatrolled;
	}
	public void setNumTimesPatrolled(int numTimesPatrolled) {
		this.numTimesPatrolled = numTimesPatrolled;
	}
	
}
