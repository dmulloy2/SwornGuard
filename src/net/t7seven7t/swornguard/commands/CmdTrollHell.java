/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;

/**
 * @author t7seven7t
 */
public class CmdTrollHell extends SwornGuardCommand {

	public CmdTrollHell(SwornGuard plugin) {
		super(plugin);
		this.name = "troll";
		this.aliases.add("hell");
		this.aliases.add("trollhell");
		this.description = "Put someone in troll hell ;)";
		this.permission = PermissionType.CMD_TROLL.permission;
		this.requiredArgs.add("player");
		this.usesPrefix = false;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(args[0]);
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);
		if (data == null)
			return;
		
		data.setTrollHell(!data.isTrollHell());
		
		if (target.isOnline()) {
			Player troll = target.getPlayer();
			
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (!plugin.getPermissionHandler().hasPermission(p, PermissionType.TROLL_SPY.permission)) {
					if (data.isTrollHell()) {
						p.hidePlayer(troll);
						troll.hidePlayer(p);
					} else {
						p.showPlayer(troll);
						troll.showPlayer(p);
					}
				}
			}
		}
		
		String result = FormatUtil.format(plugin.getMessage("troll_hell"), target.getName(), data.isTrollHell() ? "in" : "freed from");
		sendMessage(result);
		plugin.getLogHandler().log(result);
	}

}
