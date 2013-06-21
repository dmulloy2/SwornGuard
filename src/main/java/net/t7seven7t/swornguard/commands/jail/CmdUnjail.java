/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import org.bukkit.OfflinePlayer;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;

/**
 * @author t7seven7t
 */
public class CmdUnjail extends SwornGuardCommand {

	public CmdUnjail(SwornGuard plugin) {
		super(plugin);
		this.name = "unjail";
		this.description = plugin.getMessage("desc_unjail");
		this.permission = PermissionType.CMD_UNJAIL.permission;
		this.requiredArgs.add("player");
	}
	
	public void perform() {
		OfflinePlayer target = getTarget(args[0]);
		if (target == null)
			return;
		
		PlayerData data = plugin.getPlayerDataCache().getData(target);
		
		if (data.isJailed()) {
			plugin.getJailHandler().unjail(target, sender.getName());
			plugin.getLogHandler().log(plugin.getMessage("jail_log_unjail"), target.getName(), sender.getName());
			sendMessage(plugin.getMessage("jail_confirm_unjail"), target.getName());
		} else {
			sendMessage(plugin.getMessage("jail_not_jailed"),
						target.getName() + " is");
		}
	}
	
}