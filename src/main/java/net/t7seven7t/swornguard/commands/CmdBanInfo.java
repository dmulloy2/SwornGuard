/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.TimeUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

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
		this.permission = Permission.CMD_BAN_INFO;
		this.addOptionalArg("player");
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
		line.append(FormatUtil.format(plugin.getMessage("baninfo_header"), target.getName(),
				(target.isBanned()) ? plugin.getMessage("baninfo_banned") : plugin.getMessage("baninfo_notbanned")));
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
