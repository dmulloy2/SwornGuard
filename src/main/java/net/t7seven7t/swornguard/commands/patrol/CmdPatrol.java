/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.patrol;

import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.util.Util;

/**
 * @author t7seven7t
 */
public class CmdPatrol extends SwornGuardCommand {

	public CmdPatrol(SwornGuard plugin) {
		super(plugin);
		this.name = "patrol";
		this.aliases.add("pat");
		this.optionalArgs.add("player");
		this.description = "Teleports you to a player on the server.";
		this.permission = PermissionType.CMD_PATROL.permission;
		this.mustBePlayer = true;
	}
	
	@Override
	public void perform() {
		Player target = null;
		if (args.length > 0) {
			target = Util.matchPlayer(args[0]);
		}
		
		plugin.getPatrolHandler().patrol(player, target);
	}
	
}
