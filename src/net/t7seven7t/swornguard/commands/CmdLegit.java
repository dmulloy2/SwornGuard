/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import org.bukkit.OfflinePlayer;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;

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
	}
	
	public void perform() {
		OfflinePlayer target = null;
		if (args.length == 0 && isPlayer())
			target = player;
		else if (args.length > 0)
			target = getTarget(args[0]);
		if (target == null)
			return;
		
		plugin.getXrayDetector().legit(target);
		
		sendMessage(plugin.getMessage("legit_confirm"), target.getName());
	}

}
