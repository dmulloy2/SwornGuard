/**
 * (c) 2013 dmulloy2
 */
package net.t7seven7t.swornguard.handlers;

import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.Util;

import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */

public class LogFilterHandler implements Filter {
	private final SwornGuard plugin;
	private final boolean wrongMovementDetectorEnabled;
	private final List<String> filterMessagesFromLog;

	public LogFilterHandler(SwornGuard plugin) {
		this.plugin = plugin;

		this.wrongMovementDetectorEnabled = plugin.getConfig().getBoolean("wrongMovementDetectorEnabled");
		this.filterMessagesFromLog = plugin.getConfig().getStringList("filterMessagesFromLog");
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		String message = record.getMessage();

		// Do internal checks first
		if (message.contains("moved too quickly!")) {
			String playerName = message.split(" ")[0];
			
			Player player = Util.matchPlayer(playerName);
			if (player != null) {
				if (wrongMovementDetectorEnabled &&
						! plugin.getPermissionHandler().hasPermission(player, PermissionType.ALLOW_FLY.permission)) {
					CheatEvent event = new CheatEvent(player.getName(), CheatType.MOVED_WRONGLY, 
							FormatUtil.format(plugin.getMessage("cheat_message"), player.getName(), "moving too quickly!"));
					plugin.getCheatHandler().announceCheat(event);
				}
				
				return false;
			}
		}

		// Always show severe exceptions
		if (record.getLevel() == Level.SEVERE) {
			return true;
		}

		// Now filter messages defined in the config
		for (String filterMessage : filterMessagesFromLog) {
			if (message.contains(filterMessage)) {
				return false;
			}
		}

		return true;
	}

}
