/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.util.FormatUtil;

/**
 * @author t7seven7t
 */
public class CmdJailHelp extends SwornGuardCommand {
	
	public CmdJailHelp(SwornGuard plugin) {
		super(plugin);
		this.name = "jailhelp";
		this.description = plugin.getMessage("desc_jailhelp");
	}

	@Override
	public void perform() {
		List<String> lines = new ArrayList<String>();
		lines.add(FormatUtil.format(plugin.getMessage("jail_help_header")));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_JAIL_CHECK.permission))
			lines.add(new CmdCheck(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_JAIL.permission))
			lines.add(new CmdJail(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_JAIL_MUTE.permission))
			lines.add(new CmdMute(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_JAIL_REASON.permission))
			lines.add(new CmdReason(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_JAIL_SET.permission))
			lines.add(new CmdSet(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_JAIL_STATUS.permission))
			lines.add(new CmdStatus(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_JAIL_TIME.permission))
			lines.add(new CmdTime(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_UNJAIL.permission))
			lines.add(new CmdUnjail(plugin).getUsageTemplate(true));

		for (String line : lines)
			sendMessage(line);
	}

}
