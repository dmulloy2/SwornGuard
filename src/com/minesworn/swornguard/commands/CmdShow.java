package com.minesworn.swornguard.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;

public class CmdShow extends SGCommand {
	
	public CmdShow() {
		this.name = "show";
		this.aliases.add("s");
		this.description = "Checks a player's history.";
		this.permission = Permission.SHOW.node;
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
		
		ArrayList<String> profilerList = new ArrayList<String>();
		for (int x = i.getProfilerList().size() - 1; x >= 0; x--) {
			profilerList.add(i.getProfilerList().get(x));
		}
		
		double plength = profilerList.size();		
		int totalPages = (int) Math.ceil(plength / 10);
		
		if (page > totalPages) {
			errorMessage(SwornGuard.lang.getErrorMessage("nosuchpage"));
			return;
		}
		
		ArrayList<String> lines = new ArrayList<String>();
		
		StringBuilder ln = new StringBuilder();
		ln.append("History for " + ChatColor.GREEN + target.getName());
		lines.add(ln.toString());
		
		for (int x = ((page * 10) - 10); x < (page * 10); x++) {
			if (x < profilerList.size())
				lines.add(profilerList.get(x));
		}
		
		ln = new StringBuilder();
		ln.append(ChatColor.RED + "Page " + page + "/" + totalPages);
		lines.add(ln.toString());
		
		for (String s : lines) {
			confirmMessage(s);
		}
	}
	
}
