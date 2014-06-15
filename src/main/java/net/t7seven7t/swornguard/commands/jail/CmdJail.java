/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdJail extends SwornGuardCommand {

	public CmdJail(SwornGuard plugin) {
		super(plugin);
		this.name = "jail";
		this.description = plugin.getMessage("desc_jail");
		this.permission = Permission.CMD_JAIL;
		this.requiredArgs.add("player");
		this.requiredArgs.add("time");
		this.requiredArgs.add("reason");
	}

	@Override
	public void perform() {
		plugin.getLogHandler().debug("Checking if jail is setup..");
		
		if (! plugin.getJailHandler().getJail().isSetup()) {
			err(plugin.getMessage("jail_error_not_setup"));
			return;
		}

		plugin.getLogHandler().debug("Getting player for arg {0}..", args[0]);
		
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;
		
		plugin.getLogHandler().debug("Getting time for arg {0}...", args[1]);
		
		long time;
		try {
			time = TimeUtil.parseTime(args[1]);
		} catch (Exception e) {
			if (e.getMessage().equals("badtime")) {
				err(plugin.getMessage("jail_error_time_format"), args[1]);
			} else {
				err("Error getting jail time: {0}", e.getMessage());
				plugin.getLogHandler().debug(Util.getUsefulStack(e, "getting jail time"));
			}

			return;
		}

		if (time < 1000) {
			err(plugin.getMessage("jail_error_time_out_of_range"), args[1]);
			return;
		}
		
		plugin.getLogHandler().debug("Attempting to get jail reason.");
		
		StringBuilder reason = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			reason.append(args[i] + " ");
		}
		
		reason.deleteCharAt(reason.lastIndexOf(" "));
		
		plugin.getLogHandler().debug("Checking if player is already jailed.");
		
		if (plugin.getPlayerDataCache().getData(target).isJailed()) {
			err("{0} is already jailed.", target.getName());
			return;
		}
		
		plugin.getLogHandler().debug("Updating jail count for player.");
		
		if (isPlayer()) {
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			data.setPlayersJailed(data.getPlayersJailed() + 1);
		}
		
		plugin.getLogHandler().debug("Giving control to jail handler.");
		
		plugin.getJailHandler().jail(target, time, reason.toString(), sender.getName());
		plugin.getLogHandler().log(plugin.getMessage("jail_log_jail"), target.getName(), TimeUtil.formatTime(time), reason.toString(), sender.getName());
		sendMessage(plugin.getMessage("jail_confirm_jail"), target.getName(), time, reason.toString(), sender.getName());
		
		plugin.getLogHandler().debug("Command done.");
	}

}
