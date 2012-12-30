package com.minesworn.swornguard;

import java.util.ArrayList;

import org.bukkit.Material;

import com.minesworn.swornguard.core.io.SPersist;

public class Config {
	
	public static boolean enableFlyDetector = true;
	public static boolean enableXrayDetector = true;
	public static boolean enableSpamProtection = true;
	public static boolean enableFactionBetrayalDetector = true;
	public static boolean enableAutoClickerProtection = true;
	public static boolean enableCombatLogDetector = true;
	public static boolean enableAutoModeratorBot = true;
	
	public static boolean jailAmountNoticeEnabled = true;
	public static boolean vanishedPlayersAreVisibleToAdmins = true;
	
	public static double stoneToIronRatioBeforeWarning = 45.0D;
	public static double stoneToDiamondRatioBeforeWarning = 9.0D;
	
	public static long timeBetweenAttacksInMilliseconds = 500;
	
	public static int maxNumberOfEntitiesAllowedToDamagePerSecondBeforeWarning = 4;
	
	public static int jailsBeforeNotice = 2;
	
	public static boolean mobHitsCauseCombatLog = false;
	public static boolean playerHitsCauseCombatLog = true;
	public static double combatLogWindowLastsForXSecondsAfterLastAttack = 3.0D;
	
	public static String autoModeratorkickReasonFlying = "Remove your fly hack.";
	public static String autoModeratorkickReasonSpam = "Do not spam.";
	public static String autoModeratorkickReasonAutoAttack = "Remove your auto attack hack.";
	public static String autoModeratorkickReasonXray = "Remove your xray hack/texturepack.";
	
	public static int autoModeratorXrayJailTime = 10;
	public static int autoModeratorFactionBetrayalJailTime = 10;
	public static boolean autoModeratorAlwaysEnabled = false;
	
	public static int spamThresholdBeforeReport = 5;
	public static ArrayList<String> blockedCommands = new ArrayList<String>();
	public static ArrayList<Integer> autoclickerAllowedWeapons = new ArrayList<Integer>();
	
	public static boolean clearAllPVPStats = false;
		
	static {
		blockedCommands.add("op");
		autoclickerAllowedWeapons.add(Material.GOLD_HOE.getId());
		autoclickerAllowedWeapons.add(Material.IRON_HOE.getId());
		autoclickerAllowedWeapons.add(Material.DIAMOND_HOE.getId());
		autoclickerAllowedWeapons.add(Material.STONE_HOE.getId());
		autoclickerAllowedWeapons.add(Material.WOOD_HOE.getId());
		autoclickerAllowedWeapons.add(Material.BOOK.getId());
	}
	
	public static void save() {
		SPersist.save(Config.class);
	}
	
	public static void load() {
		SPersist.load(Config.class);
	}
	
}
