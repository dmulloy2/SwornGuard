/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.TimeUtil;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdCheck extends SwornGuardCommand {

	public CmdCheck(SwornGuard plugin) {
		super(plugin);
		this.name = "jailcheck";
		this.description = plugin.getMessage("desc_jailcheck");
		this.permission = PermissionType.CMD_JAIL_CHECK.permission;
		this.requiredArgs.add("player");
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);

		if (data.isJailed()) {
			sendMessage(plugin.getMessage("jail_check"), target.getName(), TimeUtil.formatTime(data.getJailTime()), data.getLastJailReason(),
					data.getLastJailer());
		} else {
			sendMessage(plugin.getMessage("jail_not_jailed"), target.getName() + " is");
		}
	}

}
