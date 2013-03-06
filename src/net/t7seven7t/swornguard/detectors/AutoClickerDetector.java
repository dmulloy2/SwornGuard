/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.SwornGuard;

/**
 * @author t7seven7t
 */
public class AutoClickerDetector {
	private final Map<String, Long> recentClicks;
	private final List<Integer> autoclickerAllowedWeapons;
	private final long autoclickerTimeBetweenAttacks;
	
	public AutoClickerDetector(final SwornGuard plugin) {
		this.recentClicks = new HashMap<String, Long>();
		
		this.autoclickerAllowedWeapons = plugin.getConfig().getIntegerList("autoclickerAllowedWeapons");
		this.autoclickerTimeBetweenAttacks = plugin.getConfig().getLong("autoclickerTimeBetweenAttacksInMillis");
	}
	
	public boolean isClickingTooFast(final Player player) {
		final long now = System.currentTimeMillis();
		
		if (player.getItemInHand() == null 
				|| !autoclickerAllowedWeapons.contains(player.getItemInHand().getTypeId())) {
			if (recentClicks.containsKey(player.getName()) 
					&& (now - recentClicks.get(player.getName()) < autoclickerTimeBetweenAttacks)) {
				return true;
			}
		}
		
		recentClicks.remove(player.getName());
		recentClicks.put(player.getName(), Long.valueOf(now));
		
		return false;
	}
}
