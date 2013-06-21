/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import org.bukkit.OfflinePlayer;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.TimeUtil;

/**
 * @author t7seven7t
 */
public class CmdReason extends SwornGuardCommand {

	public CmdReason(SwornGuard plugin) {
		super(plugin);
		this.name = "jailreason";
		this.description = plugin.getMessage("desc_jailreason");
		this.permission = PermissionType.CMD_JAIL_REASON.permission;
		this.requiredArgs.add("player");
		this.requiredArgs.add("reason");
	}
	
	public void perform() {
		OfflinePlayer target = getTarget(args[0]);
		if (target == null)
			return;
		
		StringBuilder reason = new StringBuilder();
		for (int i = 1; i < args.length; i++)
			reason.append(args[i] + " ");
		
		reason.deleteCharAt(reason.lastIndexOf(" "));
		
		PlayerData data = plugin.getPlayerDataCache().getData(target);
		
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