/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.patrol;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;

/**
 * @author t7seven7t
 */
public class CmdVanishList extends SwornGuardCommand {

	public CmdVanishList(SwornGuard plugin) {
		super(plugin);
		this.name = "vanishlist";
		this.description = "Shows a list of all vanished players on the server.";
		this.permission = PermissionType.CMD_VANISH_LIST.permission;
	}
	
	public void perform() {
		StringBuilder ret = new StringBuilder();
		ret.append(ChatColor.AQUA + "Vanish list: " + ChatColor.YELLOW);
		for (Player p : plugin.getServer().getOnlinePlayers()) {
			if (plugin.getPlayerDataCache().getData(p).isVanished())
				ret.append(p.getName() + ", ");
		}
		
		if (ret.lastIndexOf(",") != -1)
			ret.deleteCharAt(ret.lastIndexOf(","));
		
		sender.sendMessage(ret.toString());
	}

}