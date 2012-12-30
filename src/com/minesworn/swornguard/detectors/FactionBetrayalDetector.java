package com.minesworn.swornguard.detectors;

import java.util.HashMap;

import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;
import com.minesworn.swornguard.Cheat;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.detectors.factionbetrayal.Kick;
import com.minesworn.swornguard.events.CheatEvent;

public class FactionBetrayalDetector {

	private static HashMap<Player, Kick> possibleBetrayedPlayers = new HashMap<Player, Kick>(); 
	
	public static void checkFactionBetrayal(Player p, int damage, Player damager) {
		if (p.getHealth() - damage < 0 && possibleBetrayedPlayers.containsKey(p)) {
			Kick kick = possibleBetrayedPlayers.get(p);
			if (kick.getFaction() == FPlayers.i.get(damager).getFaction()) {
				if (kick.getTime() > (System.currentTimeMillis() - 1000L * 60 * 2)) {
					CheatEvent e = new CheatEvent(damager, 
							"[CHEATER] Possible faction betrayal. " + p.getName() + " was recently kicked from " 
					+ kick.getFaction().getTag() + " and now killed by " + damager.getName(), 
							Cheat.KICK_AND_KILL);
					SwornGuard.announceCheat(e);
					possibleBetrayedPlayers.remove(p);
				}
			}
		}
	}
	
	public static void addPossibleBetrayedPlayer(Player p, Kick k) {
		possibleBetrayedPlayers.put(p, k);
	}
	
}
