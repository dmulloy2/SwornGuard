package com.minesworn.swornguard.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.ServerInfo;
import com.minesworn.swornguard.SwornGuard;

public class CmdSInfo extends SGCommand {

	public CmdSInfo() {
		this.name = "sinfo";
		this.aliases.add("si");
		this.description = "Checks statistics about the server.";
		this.permission = Permission.SINFO.node;
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() {
		ServerInfo i = SwornGuard.serverInfo;
		ArrayList<String> lines = new ArrayList<String>();
		
		StringBuilder ln = new StringBuilder();
		ln.append("Server statistics for: " + ChatColor.RED + i.getName());
		lines.add(ln.toString());
		
		ln = new StringBuilder();
		ln.append("  Up for " + i.getUptime());
		lines.add(ln.toString());
		
		ln = new StringBuilder();
		ln.append("  Running " + i.getBukkitVersion());
		lines.add(ln.toString());
		
		ln = new StringBuilder();
		try {
			ln.append("  " + i.getPlayerCount() + "(" + i.getPlayersLoadedNowCount() + " loaded)" + " players in database. (" + i.getOnlinePlayerCount() + " online, " + i.getBanCount() + " banned, " + i.getIPBanCount() + " IP-banned)");
		} catch (Exception e) {
			ln.append("  Player database still loading.");
		}
		lines.add(ln.toString());
		
		for (String s : lines) {
			confirmMessage(s);
		}
	}
	
}
