package com.minesworn.swornguard.patrol.commands;

import org.bukkit.Bukkit;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.patrol.Patrol;

public class CmdAutoPatrol extends SPCommand {

	public CmdAutoPatrol() {
		this.name = "autopatrol";
		this.aliases.add("apat");
		this.mustBePlayer = true;
		this.description = "Teleports you to a player on the server continuously.";
		this.optionalArgs.add("interval");
		this.permission = Permission.AUTOPATROL.node;
	}
	
	@Override
	public void perform() {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		
		if (i.isAutoPatrolling()) {
			Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
				public void run() {
					Patrol.unAutoPatrol(player);
				}
			});
			return;
		}
		
		if (i.isCheaterInspecting()) {
			errorMessage(SwornGuard.lang.getErrorMessage("alreadyinspecting"));
			return;
		}
		
		int x = 20;
		
		if (args.length > 0)
			try {
				x = Integer.parseInt(args[0]);
				if (x < 5 || x > 60)
					x = 20;
			} catch (NumberFormatException e) {
				errorMessage(SwornGuard.lang.getErrorMessage("incorrectinterval"));
				return;
			}
		
		final int interval = x;
		Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
			public void run() {
				Patrol.autoPatrol(player, interval);
			}
		});
	}

}
