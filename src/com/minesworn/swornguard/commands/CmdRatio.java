package com.minesworn.swornguard.commands;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.detectors.XrayDetector;

public class CmdRatio extends SGCommand {

	public CmdRatio() {
		this.name = "ratio";
		this.description = "Checks a player's mining ratio.";
		this.permission = Permission.RATIO.node;
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
		
		confirmMessage("Mining statistics for: " + ChatColor.GREEN + target.getName());
		if (i.getDiamondMined() == 0)
			confirmMessage("Hasn't mined any diamonds.");
		else
			confirmMessage("Diamond ratio: " + XrayDetector.getDiamondRatio(target) + "(%%)");
		if (i.getIronMined() == 0)
			confirmMessage("Hasn't mined any iron ore.");
		else
			confirmMessage("Iron ratio: " + XrayDetector.getIronRatio(target) + "(%%)");
	}
	
}
