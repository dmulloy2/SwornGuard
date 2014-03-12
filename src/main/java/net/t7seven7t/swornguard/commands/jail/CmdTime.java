/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.TimeUtil;
import net.t7seven7t.swornguard.util.Util;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdTime extends SwornGuardCommand {

	public CmdTime(SwornGuard plugin) {
		super(plugin);
		this.name = "jailtime";
		this.description = plugin.getMessage("desc_jailtime");
		this.permission = PermissionType.CMD_JAIL_TIME.permission;
		this.requiredArgs.add("player");
		this.requiredArgs.add("time");
	}
	
	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);
		
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

		if (data.isJailed()) {
			data.setJailTime(time);
			data.getProfilerList().add(FormatUtil.format(	plugin.getMessage("profiler_event"), 
															TimeUtil.getLongDateCurr(), 
															FormatUtil.format(	plugin.getMessage("profiler_jail_time"), 
																				sender.getName(), TimeUtil.formatTime(time))));
			sendMessage(plugin.getMessage("jail_time"), target.getName());
		} else {
			sendMessage(plugin.getMessage("jail_not_jailed"),
						target.getName() + " is");
		}
	}
	
}
