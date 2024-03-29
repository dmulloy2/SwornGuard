/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.TimeUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdReason extends SwornGuardCommand {

	public CmdReason(SwornGuard plugin) {
		super(plugin);
		this.name = "jailreason";
		this.description = plugin.getMessage("desc_jailreason");
		this.permission = Permission.CMD_JAIL_REASON;
		this.addRequiredArg("player");
		this.addRequiredArg("reason");
	}
	
	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);

		StringBuilder reason = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			reason.append(args[i] + " ");
		
		reason.deleteCharAt(reason.lastIndexOf(" "));

		if (data.isJailed()) {
			data.setLastJailReason(reason.toString());
			data.getProfilerList().add(FormatUtil.format(	plugin.getMessage("profiler_event"), 
															TimeUtil.getLongDateCurr(), 
															FormatUtil.format(	plugin.getMessage("profiler_jail_reason"), 
																				sender.getName(), reason)));
			sendMessage(plugin.getMessage("jail_reason"), target.getName());
		} else {
			sendMessage(plugin.getMessage("jail_not_jailed"),
						target.getName() + " is");
		}
	}
	
}
