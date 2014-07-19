/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.handlers;

import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class AutoModerator {
	private final SwornGuard plugin;

	public AutoModerator(final SwornGuard plugin) {
		this.plugin = plugin;
	}

	public void manageCheatEvent(CheatEvent event) {
		PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
		String reason = null;

		switch (event.getCheat()) {
		case FLYING:
			reason = plugin.getConfig().getString("autoModKickReasonFly");
			break;
		case SPAM:
			reason = plugin.getConfig().getString("autoModKickReasonSpam");
			break;
		case SPEED:
			reason = plugin.getConfig().getString("autoModKickReasonSpeed", "Remove your speed hack.");
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
			for (Player player1 : Util.getOnlinePlayers()) {
				if (plugin.getPermissionHandler().hasPermission(player1, Permission.SHOW_CHEAT_REPORTS))
					player1.sendMessage(ChatColor.YELLOW + "AutoModBot kicked " + event.getPlayerName() + " for " + reason);
			}

			data.getProfilerList().add(FormatUtil.format(plugin.getMessage("profiler_event"), TimeUtil.getLongDateCurr(),
					FormatUtil.format(plugin.getMessage("profiler_automodbot"), player.getName(), reason)));
		}
	}

	public boolean isOnlyModOnline() {
		for (Player player : Util.getOnlinePlayers()) {
			if (plugin.getPermissionHandler().hasPermission(player, Permission.SHOW_CHEAT_REPORTS))
				return plugin.getConfig().getBoolean("autoModAlwaysEnabled");
		}

		return true;
	}

}
