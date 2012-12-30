package com.minesworn.swornguard.detectors;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.Cheat;
import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.util.Util;
import com.minesworn.swornguard.events.CheatEvent;

public class XrayDetector {

	public static void addBlock(Material mat, Player p) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
		if (mat.equals(Material.IRON_ORE))
			i.setIronMined(i.getIronMined() + 1);
		else if (mat.equals(Material.DIAMOND_ORE))
			i.setDiamondMined(i.getDiamondMined() + 1);
		else if (mat.equals(Material.STONE))
			i.setStoneMined(i.getStoneMined() + 1);
		checkRatio(p);
	}
	
	public static void checkRatio(Player p) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
		if (((i.getStoneMined() > 0 && getIronRatio(p) > Config.stoneToIronRatioBeforeWarning) 
				|| (i.getDiamondMined() > 0 && getDiamondRatio(p) > Config.stoneToDiamondRatioBeforeWarning)) 
				&& i.getStoneMined() > 150 && (System.currentTimeMillis() - i.getLastWarnedForXray() > 45000L)) {
			CheatEvent e = new CheatEvent(p, "[CHEATER] I think that " + p.getName() + " is xraying!", Cheat.XRAY);
			SwornGuard.announceCheat(e);
			i.setLastWarnedForXray(System.currentTimeMillis());
		}
	}
	
	public static double getDiamondRatio(OfflinePlayer p) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
		double ratio = (double) i.getDiamondMined() / (i.getDiamondMined() + i.getStoneMined()) * 100;
		return (Util.roundNumDecimals(ratio, 2));
	}
	
	public static double getIronRatio(OfflinePlayer p) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
		double ratio = (double) i.getIronMined() / (i.getIronMined() + i.getStoneMined()) * 100;
		return (Util.roundNumDecimals(ratio, 2));
	}
	
	public static void legit(OfflinePlayer p) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());

		i.setStoneMined(0);
		i.setIronMined(0);
		i.setDiamondMined(0);
	}
	
}
