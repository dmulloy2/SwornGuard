/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.patrol;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;

/**
 * @author t7seven7t
 */
public class CmdPatrol extends SwornGuardCommand {

	public CmdPatrol(SwornGuard plugin) {
		super(plugin);
		this.name = "patrol";
		this.aliases.add("pat");
		this.mustBePlayer = true;
		this.description = "Teleports you to a player on the server.";
		this.permission = PermissionType.CMD_PATROL.permission;
	}
	
	@Override
	public void perform() {
		plugin.getPatrolHandler().patrol(player);
	}
	
}
