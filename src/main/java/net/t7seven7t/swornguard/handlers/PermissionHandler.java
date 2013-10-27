/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.handlers;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class PermissionHandler {
	private final SwornGuard plugin;
	
	public PermissionHandler(SwornGuard plugin) {
		this.plugin = plugin;
	}

	public boolean hasPermission(CommandSender sender, Permission permission) {
		return (permission == null) ? true : hasPermission(sender, getPermissionString(permission));
	}

	public boolean hasPermission(CommandSender sender, String permission) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			return (p.hasPermission(permission) || p.isOp());
		}
		
		return true;
	}
	
	public String getPermissionString(Permission permission) {
		return plugin.getName().toLowerCase() + "." + permission.getNode().toLowerCase();
	}

}
