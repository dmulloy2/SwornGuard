/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands.patrol;

import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.types.Permission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class CmdVanishList extends SwornGuardCommand {

	public CmdVanishList(SwornGuard plugin) {
		super(plugin);
		this.name = "vanishlist";
		this.description = "Shows a list of all vanished players on the server.";
		this.permission = Permission.CMD_VANISH_LIST;
	}

	@Override
	public void perform() {
		StringBuilder ret = new StringBuilder();
		ret.append(ChatColor.AQUA + "Vanish list: " + ChatColor.YELLOW);
		for (Player p : Util.getOnlinePlayers()) {
			if (plugin.getPlayerDataCache().getData(p).isVanished())
				ret.append(p.getName() + ", ");
		}

		if (ret.lastIndexOf(",") != -1)
			ret.deleteCharAt(ret.lastIndexOf(","));

		sender.sendMessage(ret.toString());
	}

}
