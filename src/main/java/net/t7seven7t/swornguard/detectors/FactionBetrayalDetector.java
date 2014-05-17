/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import java.util.HashMap;
import java.util.Map;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.FactionKick;

import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayers;

/**
 * @author t7seven7t
 */
public class FactionBetrayalDetector {
	private final SwornGuard plugin;
	private final Map<String, FactionKick> possibleBetrayedPlayers;

	public FactionBetrayalDetector(final SwornGuard plugin) {
		this.plugin = plugin;
		this.possibleBetrayedPlayers = new HashMap<String, FactionKick>();
	}

	public void check(Player player, int damage, Player damager) {
		if (player.getHealth() - damage < 0 && possibleBetrayedPlayers.containsKey(player.getName())) {
			FactionKick kick = possibleBetrayedPlayers.get(player.getName());
			if (kick.getFaction() == FPlayers.i.get(damager).getFaction()) {

				// Check if player was kicked from faction within 5mins ago
				if (kick.getTime() > (System.currentTimeMillis() - 1000L * 60 * 5)) {
					CheatEvent event = new CheatEvent(damager, CheatType.KICK_AND_KILL, "[CHEATER] Possible faction betrayal. "
							+ player.getName() + " was recently kicked from " + kick.getFaction().getTag() + " and now killed by "
							+ damager.getName());
					plugin.getCheatHandler().announceCheat(event);
					possibleBetrayedPlayers.remove(player.getName());
				}
			}
		}
	}

	public void addPossibleBetrayedPlayer(final String player, final FactionKick kick) {
		possibleBetrayedPlayers.put(player, kick);
	}
}