package com.minesworn.swornguard.detectors;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.Cheat;
import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.events.CheatEvent;

public class CombatLogDetector {

	public static void check(Player p) {
		long now = System.currentTimeMillis();
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
				
		if ((now - i.getLastAttacked()) < Config.combatLogWindowLastsForXSecondsAfterLastAttack * 1000L) {
			CheatEvent e = new CheatEvent(p, "[CHEATER] " + p.getName() + " just combat logged!", Cheat.COMBAT_LOG);
			SwornGuard.announceCheat(e);
			p.setHealth(0);
		}
	}
	
	public static void addAttacked(Player p, Entity attacker) {
		long now = System.currentTimeMillis();
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
		
		if (attacker instanceof Monster && Config.mobHitsCauseCombatLog)
			i.setLastAttacked(now);
		else if (attacker instanceof Player && Config.playerHitsCauseCombatLog) {
			i.setLastAttacked(now);
		}
	}
	
}
