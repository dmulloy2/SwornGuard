/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdLegit extends SwornGuardCommand {

	public CmdLegit(SwornGuard plugin) {
		super(plugin);
		this.name = "legit";
		this.description = plugin.getMessage("desc_legit");
		this.permission = PermissionType.CMD_LEGIT.permission;
		this.optionalArgs.add("player");
		this.usesPrefix = true;
	}
	
	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;
		
		plugin.getXrayDetector().legit(target);
		
		sendMessage(plugin.getMessage("legit_confirm"), target.getName());
	}

}
