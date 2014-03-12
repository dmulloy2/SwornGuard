/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.TimeUtil;
import net.t7seven7t.swornguard.util.Util;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdBanInfo extends SwornGuardCommand {

	public CmdBanInfo(SwornGuard plugin) {
		super(plugin);
		this.name = "baninfo";
		this.aliases.add("bi");
		this.description = plugin.getMessage("desc_baninfo");
		this.permission = PermissionType.CMD_BAN_INFO.permission;
		this.optionalArgs.add("player");
		this.usesPrefix = true;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);

		List<String> lines = new ArrayList<String>();
		
		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(	plugin.getMessage("baninfo_header"), 
										target.getName(), 
										(Util.isBanned(target)) ? plugin.getMessage("baninfo_banned") : plugin.getMessage("baninfo_notbanned")));
		lines.add(line.toString());
		
		if (data.getBans() != 0) {
			line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("baninfo_last_ban"), 
						data.getLastBanner(), 
						TimeUtil.getSimpleDate(data.getLastBan()), 
						TimeUtil.formatTimeDifference(data.getLastBan(), System.currentTimeMillis())));
			lines.add(line.toString());
			
			line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("baninfo_ban_reason"), data.getLastBanReason()));
			lines.add(line.toString());
		}
		
		if (data.getLastUnban() != 0) {
			line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("baninfo_last_unban"),
						data.getLastUnbanner(),
						TimeUtil.getSimpleDate(data.getLastUnban()),
						TimeUtil.formatTimeDifference(data.getLastUnban(), System.currentTimeMillis())));
			lines.add(line.toString());
			
			line = new StringBuilder();
			line.append(FormatUtil.format(plugin.getMessage("baninfo_unban_reason"), data.getLastUnbanReason()));
			lines.add(line.toString());
		}
		
		for (String string : lines)
			sendMessage(string);
	}

}
