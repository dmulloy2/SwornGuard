/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;

/**
 * @author t7seven7t
 */
public class CmdReload extends SwornGuardCommand {

	public CmdReload(SwornGuard plugin) {
		super(plugin);
		this.name = "reload";
		this.description = plugin.getMessage("desc_reload");
		this.permission = PermissionType.CMD_RELOAD.permission;
		this.usesPrefix = true;
	}
	
	@Override
	public void perform() {
		plugin.onDisable();
		plugin.onEnable();
		sendMessage(plugin.getMessage("reload_confirm"));
		plugin.getLogHandler().log(plugin.getMessage("reload_log"), sender.getName());
	}
	
}
