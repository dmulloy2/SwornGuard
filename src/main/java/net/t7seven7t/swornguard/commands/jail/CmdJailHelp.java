/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.jail;

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
		plugin.getHelpCommand().execute(sender, args);
	}

}