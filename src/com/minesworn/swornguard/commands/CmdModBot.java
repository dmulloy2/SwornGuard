package com.minesworn.swornguard.commands;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.SwornGuard;

public class CmdModBot extends SGCommand {

	public CmdModBot() {
		this.name = "modbot";
		this.aliases.add("mb");
		this.description = "Enables/disable the automatic mod bot.";
		this.permission = Permission.MOD_BOT.node;
		this.optionalArgs.add("on/off");
	}
	
	public void perform() {		
		boolean enable = !SwornGuard.autoModBotEnabled;
		if (args.length == 1) {
			if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("enable"))
				enable = true;
			else if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("disable"))
				enable = false;
		}
		
		SwornGuard.autoModBotEnabled = enable;
		confirmMessage("AutoModBot was " + ((enable) ? "enabled" : "disabled") + "!");
	}
	
}
