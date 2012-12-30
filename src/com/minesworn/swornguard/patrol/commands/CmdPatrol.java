package com.minesworn.swornguard.patrol.commands;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.patrol.Patrol;

public class CmdPatrol extends SPCommand {

	public CmdPatrol() {
		this.name = "patrol";
		this.aliases.add("pat");
		this.mustBePlayer = true;
		this.description = "Teleports you to a player on the server.";
		this.permission = Permission.PATROL.node;
	}
	
	public void perform() {
		Patrol.patrol(player);
	}

}
