/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;

/**
 * @author t7seven7t
 */
public class CmdSet extends SwornGuardCommand {

	public CmdSet(SwornGuard plugin) {
		super(plugin);
		this.name = "jailset";
		this.permission = PermissionType.CMD_JAIL_SET.permission;
		this.description = plugin.getMessage("desc_jailset");
		this.optionalArgs.add("cancel");
	}

	public void perform() {
		if (args.length == 1) {
			plugin.getJailHandler().getJail().resetJailStage();
			sendMessage(plugin.getMessage("jail_cancel_set"));
			return;
		}
		
		switch (plugin.getJailHandler().getJail().getJailStage()) {
		case 0:
			sendMessage(plugin.getMessage("jail_set_1"));
			break;
		case 1:
			plugin.getJailHandler().getJail().setWorld(player.getWorld());
			plugin.getJailHandler().getJail().setMin(player.getLocation().toVector().toBlockVector());
			sendMessage(plugin.getMessage("jail_set_2"));
			break;
		case 2:
			plugin.getJailHandler().getJail().setMax(player.getLocation().toVector().toBlockVector());
			sendMessage(plugin.getMessage("jail_set_3"));
			break;
		case 3:
			plugin.getJailHandler().getJail().setSpawn(player.getLocation());
			sendMessage(plugin.getMessage("jail_set_4"));
			break;
		case 4:
			plugin.getJailHandler().getJail().setExit(player.getLocation());
			sendMessage(plugin.getMessage("jail_set_5"));
			break;
		default:
			break;
		}
		
		plugin.getJailHandler().getJail().nextJailStage();
	}
	
}
