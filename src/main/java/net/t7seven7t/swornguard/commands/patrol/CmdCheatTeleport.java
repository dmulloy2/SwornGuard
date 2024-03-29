/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.patrol;

import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class CmdCheatTeleport extends SwornGuardCommand {

	public CmdCheatTeleport(SwornGuard plugin) {
		super(plugin);
		this.name = "ctp";
		this.mustBePlayer = true;
		this.description = "Teleport to hackers.";
		this.addOptionalArg("player");
		this.permission = Permission.CMD_CHEAT_TELEPORT;
	}

	@Override
	public void perform() {
		if (args.length > 0) {
			final Player target = Util.matchPlayer(args[0]);
			if (target != null && plugin.getPatrolHandler().getRecentCheaters().contains(target.getName())) {
				plugin.getPatrolHandler().cheaterTeleport(player, target);
			} else {
				err(plugin.getMessage("error_player_not_cheating"));
			}
		} else {
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			if (data.isInspecting()) {
				plugin.getPatrolHandler().returnFromInspecting(player);
				sendMessage("&eYou are no longer inspecting!");
			}
		}
	}
	
}
