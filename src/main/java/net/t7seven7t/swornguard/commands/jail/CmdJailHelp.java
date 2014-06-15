/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.util.FormatUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.types.Permission;

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
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_JAIL_CHECK))
			lines.add(new CmdCheck(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_JAIL))
			lines.add(new CmdJail(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_JAIL_MUTE))
			lines.add(new CmdMute(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_JAIL_REASON))
			lines.add(new CmdReason(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_JAIL_SET))
			lines.add(new CmdSet(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_JAIL_STATUS))
			lines.add(new CmdStatus(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_JAIL_TIME))
			lines.add(new CmdTime(plugin).getUsageTemplate(true));
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_UNJAIL))
			lines.add(new CmdUnjail(plugin).getUsageTemplate(true));

		for (String line : lines)
			sendMessage(line);
	}

}
