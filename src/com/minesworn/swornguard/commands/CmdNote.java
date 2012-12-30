package com.minesworn.swornguard.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.core.util.Util;
import com.minesworn.swornguard.PlayerInfo;

public class CmdNote extends SGCommand {

	public CmdNote() {
		this.name = "note";
		this.aliases.add("n");
		this.description = "Adds a note to a player's profile.";
		this.permission = Permission.NOTE.node;
		this.requiredArgs.add("player");
		this.requiredArgs.add("note");
	}
	
	public void perform() {
		OfflinePlayer target = getTarget(args[0]);
		if (target == null)
			return;
			
		PlayerInfo i = getPlayerInfo(target);
		if (i == null)
			return;
		
		StringBuilder ln = new StringBuilder();
		ln.append("[" + Util.getLongDateCurr() + " GMT] " + ChatColor.GREEN + player.getName() + ": ");
		for (int x = 1; x < args.length; x++) {
			ln.append(args[x] + " ");
		}
		
		i.getProfilerList().add(ln.toString());
		confirmMessage("Note added!");
	}
	
}
