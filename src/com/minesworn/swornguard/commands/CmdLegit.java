package com.minesworn.swornguard.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.detectors.XrayDetector;

public class CmdLegit extends SGCommand {

	public CmdLegit() {
		this.name = "legit";
		this.description = "Clears a player's mined ore count.";
		this.permission = Permission.LEGIT.node;
		this.optionalArgs.add("player");
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
		
		PlayerInfo i = getPlayerInfo(target);
		if (i == null)
			return;
		
		XrayDetector.legit(target);
		
		confirmMessage(ChatColor.GREEN + target.getName() + ChatColor.YELLOW + "'s mining stats have been cleared.");
	}
	
}
