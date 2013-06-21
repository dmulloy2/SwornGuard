/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.TimeUtil;

/**
 * @author t7seven7t
 */
public class CmdStatus extends SwornGuardCommand {

	public CmdStatus(SwornGuard plugin) {
		super(plugin);
		this.name = "jailstatus";
		this.description = plugin.getMessage("desc_jailstatus");
		this.mustBePlayer = true;
		this.permission = PermissionType.CMD_JAIL_STATUS.permission;
	}
	
	public void perform() {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		if (data.isJailed()) {
			sendMessage(plugin.getMessage("jail_status"), 
						TimeUtil.formatTime(data.getJailTime()), 
						data.getLastJailReason(), 
						data.getLastJailer());

		} else {
			sendMessage(plugin.getMessage("jail_not_jailed"),
						"You are");
		}
	}
	
}
