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
public class CmdJail extends SwornGuardCommand {

	public CmdJail(SwornGuard plugin) {
		super(plugin);
		this.name = "jail";
		this.description = plugin.getMessage("desc_jail");
		this.permission = PermissionType.CMD_JAIL.permission;
		this.requiredArgs.add("player");
		this.requiredArgs.add("time");
		this.requiredArgs.add("reason");
	}

	@Override
	public void perform() {
		if (plugin.isDebug())
			plugin.getLogHandler().log("Checking if jail is setup..");
		
		if (!plugin.getJailHandler().getJail().isSetup()) {
			err(plugin.getMessage("jail_error_not_setup"));
			return;
		}
		
		if (plugin.isDebug())
			plugin.getLogHandler().log("Getting player for arg {0}..", args[0]);
		
		OfflinePlayer target = getTarget(args[0]);
		if (target == null)
			return;
		
		if (plugin.isDebug())
			plugin.getLogHandler().log("Getting time for arg {0}...", args[1]);
		
		long time;
		try {
			time = TimeUtil.parseTime(args[1]);
		} catch (Exception ex) {
			if (ex != null && ex.getMessage() != null && ex.getMessage().equals("badtime"))
				err(plugin.getMessage("jail_error_time_format"), args[1]);
			else if (plugin.isDebug()) {
				ex.printStackTrace();
				plugin.getLogHandler().log("Caught unhandled exception getting time.");
			}
			return;
		}
		
		if (time < 1000) {
			err(plugin.getMessage("jail_error_time_out_of_range"), args[1]);
			return;
		}
		
		if (plugin.isDebug())
			plugin.getLogHandler().log("Attempting to get jail reason.");
		
		StringBuilder reason = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			reason.append(args[i] + " ");
		}
		
		reason.deleteCharAt(reason.lastIndexOf(" "));
		
		if (plugin.isDebug())
			plugin.getLogHandler().log("Checking if player is already jailed.");
		
		if (plugin.getPlayerDataCache().getData(target).isJailed()) {
			err("{0} is already jailed.", target.getName());
			return;
		}
		
		if (plugin.isDebug())
			plugin.getLogHandler().log("Updating jail count for player.");
		
		if (isPlayer()) {
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			data.setPlayersJailed(data.getPlayersJailed() + 1);
		}
		
		if (plugin.isDebug())
			plugin.getLogHandler().log("Giving control to jail handler.");
		
		plugin.getJailHandler().jail(target, time, reason.toString(), sender.getName());
		plugin.getLogHandler().log(plugin.getMessage("jail_log_jail"), target.getName(), TimeUtil.formatTime(time), reason.toString(), sender.getName());
		sendMessage(plugin.getMessage("jail_confirm_jail"), target.getName(), time, reason.toString(), sender.getName());
		
		if (plugin.isDebug())
			plugin.getLogHandler().log("Command done.");
	}

}
