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
public class CmdMute extends SwornGuardCommand {

	public CmdMute(SwornGuard plugin) {
		super(plugin);
		this.name = "jailmute";
		this.description = plugin.getMessage("desc_jailmute");
		this.permission = PermissionType.CMD_JAIL_MUTE.permission;
		this.requiredArgs.add("player");
	}

	public void perform() {
		OfflinePlayer target = getTarget(args[0]);
		if (target == null)
			return;
		
		PlayerData data = plugin.getPlayerDataCache().getData(target);
		
		if (data.isJailed()) {
			if (data.isJailMuted()) {
				data.setJailMuted(false);
				sendMessage(plugin.getMessage("jail_muted"), target.getName(), "no longer");
			} else {
				data.setJailMuted(true);
				sendMessage(plugin.getMessage("jail_muted"), target.getName(), "now");
			}
		} else {
			sendMessage(plugin.getMessage("jail_not_jailed"),
						target.getName() + " is");
		}
	}

}
