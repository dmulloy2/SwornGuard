/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.util.MaterialUtil;
import net.t7seven7t.swornguard.SwornGuard;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class AutoClickerDetector {
	private final Map<String, Long> recentClicks;
	private final List<Material> autoclickerAllowedWeapons;
	private final long autoclickerTimeBetweenAttacks;
	
	public AutoClickerDetector(final SwornGuard plugin) {
		this.recentClicks = new HashMap<String, Long>();

		this.autoclickerTimeBetweenAttacks = plugin.getConfig().getLong("autoclickerTimeBetweenAttacksInMillis");
		
		this.autoclickerAllowedWeapons = new ArrayList<Material>();
		
		for (String s : plugin.getConfig().getStringList("autoclickerAllowedWeapons"))
		{
			autoclickerAllowedWeapons.add(MaterialUtil.getMaterial(s));
		}
	}

	public boolean isClickingTooFast(final Player player) {
		final long now = System.currentTimeMillis();
		
		if (player.getItemInHand() == null 
				|| !autoclickerAllowedWeapons.contains(player.getItemInHand().getType())) {
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