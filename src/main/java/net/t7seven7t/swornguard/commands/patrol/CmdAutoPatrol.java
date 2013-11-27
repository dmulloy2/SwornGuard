/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.patrol;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;

/**
 * @author t7seven7t
 */
public class CmdAutoPatrol extends SwornGuardCommand {

	public CmdAutoPatrol(SwornGuard plugin) {
		super(plugin);
		this.name = "autopatrol";
		this.aliases.add("apat");
		this.mustBePlayer = true;
		this.description = "Teleports you to a player on the server continuously.";
		this.optionalArgs.add("interval");
		this.permission = PermissionType.CMD_AUTO_PATROL.permission;
	}

	@Override
	public void perform() {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		if (data.isPatrolling() && ! data.isCooldownPatrolling()) {
			plugin.getPatrolHandler().unAutoPatrol(player);
			return;
		}
		
		if (data.isInspecting()) {
			plugin.getPatrolHandler().returnFromInspecting(player);
			return;
		}
		
		int x = 20;
		
		if (args.length > 0)
			try {
				x = Integer.parseInt(args[0]);
				if (x < 5 || x > 60)
					x = 20;
			} catch (NumberFormatException ex) {
				err(plugin.getMessage("error_interval"));
				return;
			}
		
		plugin.getPatrolHandler().autoPatrol(player, x);
	}

}
