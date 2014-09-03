/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

import net.dmulloy2.commands.Command;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;

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
		Command help = plugin.getCommandHandler().getCommand("help");
		if (help == null) {
			err("Help command does not exist!");
			return;
		}

		help.execute(sender, args);
	}

}