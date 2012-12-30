package com.minesworn.swornguard.patrol.commands;

import org.bukkit.Bukkit;

import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.patrol.Patrol;

public class CmdVanish extends SPCommand {

	public CmdVanish() {
		this.name = "vanish";
		this.aliases.add("unvanish");
		this.aliases.add("hide");
		this.aliases.add("unhide");
		this.mustBePlayer = true;
		this.description = "Vanishes you from other players.";
		this.optionalArgs.add("on/off");
		this.permission = Permission.VANISH.node;
	}
	
	@Override
	public void perform() {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		
		boolean b = !i.isVanished();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("on"))
				b = true;
			else if (args[0].equalsIgnoreCase("off"))
				b = false;
		}
		
		final boolean vanish = b;
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
			public void run() {
				Patrol.vanish(player, vanish);
			}
		});
	}

}
