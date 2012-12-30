package com.minesworn.swornguard.patrol.commands;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.core.util.Util;
import com.minesworn.swornguard.patrol.Patrol;

public class CmdCheatTeleport extends SPCommand {

	public CmdCheatTeleport() {
		this.name = "ctp";
		this.mustBePlayer = true;
		this.description = "Teleport to hackers.";
		this.optionalArgs.add("player");
		this.permission = Permission.RESPOND_CHEAT_DETECTOR.node;
	}
	
	@Override
	public void perform() {
		if (args.length > 0) {
			final Player target = Util.matchPlayer(args[0]);
			if (target != null && Patrol.recentCheaters.contains(target))
				Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
					public void run() {
						Patrol.cheaterTeleport(player, target);
					}
				});
		} else {
			PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
			if (i.isCheaterInspecting())
				Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
					public void run() {
						Patrol.returnFromCheatInspecting(player);
					}
				});
		}
	}
	
}
