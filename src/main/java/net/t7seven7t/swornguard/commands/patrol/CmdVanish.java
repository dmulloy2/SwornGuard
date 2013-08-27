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
public class CmdVanish extends SwornGuardCommand {

	public CmdVanish(SwornGuard plugin) {
		super(plugin);
		this.name = "vanish";
		this.aliases.add("unvanish");
		this.aliases.add("hide");
		this.aliases.add("unhide");
		this.mustBePlayer = true;
		this.description = "Vanishes you from other players.";
		this.optionalArgs.add("on/off");
		this.permission = PermissionType.CMD_VANISH.permission;
	}

	@Override
	public void perform() {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		boolean vanish = !data.isVanished();
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("on"))
				vanish = true;
			else if (args[0].equalsIgnoreCase("off"))
				vanish = false;
		}
		
		plugin.getPatrolHandler().vanish(player, vanish);
	}

}
