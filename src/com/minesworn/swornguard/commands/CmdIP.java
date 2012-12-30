package com.minesworn.swornguard.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;

public class CmdIP extends SGCommand {

	public CmdIP() {
		this.name = "ip";
		this.description = "Checks a player's ip history.";
		this.permission = Permission.CAN_CHECK_IPS.node;
		this.optionalArgs.add("player");
		this.optionalArgs.add("page");
		this.mustBePlayer = true;
	}
	
	public void perform() {
		OfflinePlayer target;
		if (args.length == 0)
			target = player;
		else {
			target = getTarget(args[0]);
			if (target == null)
				return;
		}
		
		int page = 1;
		
		if (args.length == 2) {
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				errorMessage(SwornGuard.lang.getErrorMessage("incorrectpagesyntax"));
				return;
			}
		}
		
		PlayerInfo i = getPlayerInfo(target);
		if (i == null)
			return;
		
		ArrayList<String> ipList = new ArrayList<String>();
		for (int x = i.getIpAddressList().size() - 1; x >= 0; x--) {
			ipList.add(i.getIpAddressList().get(x));
		}

		double iplength = ipList.size();		
		int totalPages = (int) Math.ceil(iplength / 10);
		
		if (page > totalPages) {
			errorMessage(SwornGuard.lang.getErrorMessage("nosuchpage"));
			return;
		}
		
		ArrayList<String> lines = new ArrayList<String>();
		
		StringBuilder ln = new StringBuilder();
		ln.append("IP History for " + ChatColor.GREEN + target.getName());
		lines.add(ln.toString());
		
		for (int x = ((page * 10) - 10); x < (page * 10); x++) {
			if (x < ipList.size())
				lines.add(ipList.get(x));
		}
		
		ln = new StringBuilder();
		ln.append(ChatColor.RED + "Page " + page + "/" + totalPages);
		lines.add(ln.toString());
		
		for (String s : lines) {
			confirmMessage(s);
		}
	}
	
}
