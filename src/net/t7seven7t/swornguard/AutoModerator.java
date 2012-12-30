/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;

/**
 * @author t7seven7t
 */
public class AutoModerator {
	private final SwornGuard plugin;
	
	public AutoModerator(final SwornGuard plugin) {
		this.plugin = plugin;
	}
	
	public void manageCheatEvent(CheatEvent event) {
		PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayerName());
		String reason = null;
		
		switch (event.getCheat()) {
		case FLYING:
			reason = plugin.getConfig().getString("autoModKickReasonFly");
			break;
		case KICK_AND_KILL:
			// TODO: Jail sum ppls
			break;
		case SPAM:
			reason = plugin.getConfig().getString("autoModKickReasonSpam");
			break;
		case XRAY:
			reason = plugin.getConfig().getString("autoModKickReasonXray");
			break;
		default:
			break;
		}
		
		if (reason != null) {
			Player player = plugin.getServer().getPlayerExact(event.getPlayerName());
			if (player == null) return;
			player.kickPlayer(reason);
			data.setLastKick(System.currentTimeMillis());
			data.setLastKicker("AutoModBot");
			data.setLastKickReason(reason);
			plugin.getLogHandler().log("Player {0} was kicked by AutoModBot for: {1}", event.getPlayerName(), reason);
			for (Player player1 : plugin.getServer().getOnlinePlayers()) {
				if (plugin.getPermissionHandler().hasPermission(player1, PermissionType.SHOW_CHEAT_REPORTS.permission))
					player1.sendMessage(ChatColor.YELLOW + "AutoModBot kicked " + event.getPlayerName() + " for " + reason);
			}
		}
	}
	
	public boolean isOnlyModOnline() {
		for (Player player : plugin.getServer().getOnlinePlayers()) {
			if (plugin.getPermissionHandler().hasPermission(player, PermissionType.SHOW_CHEAT_REPORTS.permission))
				return plugin.getConfig().getBoolean("autoModAlwaysEnabled");
		}
		
		return true;
	}
	
}
