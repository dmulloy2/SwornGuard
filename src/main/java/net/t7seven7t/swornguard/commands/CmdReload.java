/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import net.dmulloy2.types.Reloadable;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;

/**
 * @author t7seven7t
 */
public class CmdReload extends SwornGuardCommand implements Reloadable {

	public CmdReload(SwornGuard plugin) {
		super(plugin);
		this.name = "reload";
		this.aliases.add("rl");
		this.description = plugin.getMessage("desc_reload");
		this.permission = Permission.CMD_RELOAD;
		this.usesPrefix = true;
	}
	
	@Override
	public void perform() {
		reload(); // Deal with it :3
	}

	@Override
	public void reload() {
		plugin.reload();
		sendMessage(plugin.getMessage("reload_confirm"));
		plugin.getLogHandler().log(plugin.getMessage("reload_log"), sender.getName());
	}
	
}
