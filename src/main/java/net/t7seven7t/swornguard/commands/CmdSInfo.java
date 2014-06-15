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
import net.t7seven7t.swornguard.types.ServerData;

/**
 * @author t7seven7t
 */
public class CmdSInfo extends SwornGuardCommand {

	public CmdSInfo(SwornGuard plugin) {
		super(plugin);
		this.name = "sinfo";
		this.aliases.add("si");
		this.description = plugin.getMessage("desc_sinfo");
		this.permission = Permission.CMD_SERVER_INFO;
		this.usesPrefix = true;
	}
	
	@Override
	public void perform() {
		ServerData data = plugin.getServerData();
		List<String> lines = new ArrayList<String>();
		
		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("sinfo_header"), data.getServerName()));
		lines.add(line.toString());
		
		line = new StringBuilder();
		line.append("  " + FormatUtil.format(plugin.getMessage("sinfo_uptime"), TimeUtil.formatTime(data.getUptime())));
		lines.add(line.toString());
		
		line = new StringBuilder();
		line.append("  " + FormatUtil.format(plugin.getMessage("sinfo_bukkitver"), data.getBukkitVersion()));
		lines.add(line.toString());
		
		line = new StringBuilder();
		line.append("  " + FormatUtil.format(plugin.getMessage("sinfo_playercounts"), 
					data.getPlayerCount(), 
					data.getPlayerCacheSize(), 
					data.getOnlinePlayerCount(),
					data.getBannedPlayerCount(),
					data.getIPBanCount()));
		lines.add(line.toString());
		
		for (String string : lines)
			sendMessage(string);
	}
	
}
