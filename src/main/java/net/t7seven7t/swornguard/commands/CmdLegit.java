/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdLegit extends SwornGuardCommand {

	public CmdLegit(SwornGuard plugin) {
		super(plugin);
		this.name = "legit";
		this.description = plugin.getMessage("desc_legit");
		this.permission = Permission.CMD_LEGIT;
		this.addOptionalArg("player");
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
