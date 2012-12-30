/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;

/**
 * @author t7seven7t
 */
public class CombatLogDetector {
	private final SwornGuard plugin;
	private final long combatLogWindow;
	
	public CombatLogDetector(SwornGuard plugin) {
		this.plugin = plugin;
		this.combatLogWindow = plugin.getConfig().getInt("combatLogWindowInSeconds") * 1000L;
	}
	
	public void check(Player player) {
		long now = System.currentTimeMillis();
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		if (now - data.getLastAttacked() < combatLogWindow) {
			CheatEvent event = new CheatEvent(player.getName(), CheatType.COMBAT_LOG, 
					FormatUtil.format(plugin.getMessage("cheat_combat_log"), player.getName()));
			plugin.getCheatHandler().announceCheat(event);
			player.setHealth(0);
		}
	}
	
}
