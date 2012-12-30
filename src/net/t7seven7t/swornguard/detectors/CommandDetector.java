/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.TimeUtil;
import net.t7seven7t.swornguard.util.Util;

/**
 * @author t7seven7t
 */
public class CommandDetector {
	private final SwornGuard plugin;
	
	public CommandDetector(final SwornGuard plugin) {
		this.plugin = plugin;
	}
	
	public void checkCommand(CommandSender sender, String command, String[] args) {
		command = command.toLowerCase();
		if(args.length > 0 && (command.equals("ban") || command.equals("eban") || command.equals("tempban") || command.equals("unban") ||
				command.equals("unban") || command.equals("kick"))) {
			OfflinePlayer target = Util.matchOfflinePlayer(args[0]);
			if (target == null)
				return;
						
			PlayerData data = plugin.getPlayerDataCache().getData(target);
			PlayerData senderData = null;
			if (sender instanceof Player)
				senderData = plugin.getPlayerDataCache().getData(sender.getName());
			
			String action;

			StringBuilder reason = null;
			if (args.length > 1) {
				reason = new StringBuilder();
				for (int i = 1; i < args.length; i++)
					reason.append(args[i] + " ");
				reason.deleteCharAt(reason.lastIndexOf(" "));
			}
			
			if ((command.equals("ban") || command.equals("eban") || command.equals("tempban")) 
					&& plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_BAN.permission)) {
				action = plugin.getMessage("profiler_ban");
				data.setBans(data.getBans() + 1);
				data.setLastBan(System.currentTimeMillis());
				data.setLastBanner(sender.getName());
				data.setLastBanReason(reason == null ? "" : reason.toString());
				if (senderData != null)
					senderData.setPlayersBanned(senderData.getPlayersBanned() + 1);
			} else if ((command.equals("unban") || command.equals("eunban"))
					&& plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_UNBAN.permission)) {
				action = plugin.getMessage("profiler_unban");
				data.setLastUnban(System.currentTimeMillis());
				data.setLastUnbanner(sender.getName());
				data.setLastUnbanReason(reason == null ? "" : reason.toString());
			} else if (command.equals("kick") && plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_KICK.permission)) {
				action = plugin.getMessage("profiler_kick");
				data.setKicks(data.getKicks() + 1);
				data.setLastKick(System.currentTimeMillis());
				data.setLastKicker(sender.getName());
				data.setLastKickReason(reason == null ? "" : reason.toString());
				if (senderData != null)
					senderData.setPlayersKicked(senderData.getPlayersKicked() + 1);
			} else {
				return;
			}
			
			data.getProfilerList().add(FormatUtil.format(plugin.getMessage("profiler_event"), TimeUtil.getLongDateCurr(), 
					FormatUtil.format(plugin.getMessage("profiler_action"), target.getName(), action,
							sender.getName(), reason == null ? "" : " for " + reason.toString())));
		}
	}
	
}
