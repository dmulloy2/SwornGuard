/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.TrollType;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
public class CmdTrollHell extends SwornGuardCommand {

	public CmdTrollHell(SwornGuard plugin) {
		super(plugin);
		this.name = "trollhell";
		this.aliases.add("troll");
		this.aliases.add("hell");
		this.requiredArgs.add("player");
		this.description = "Put someone in troll hell ;)";
		this.permission = PermissionType.CMD_TROLL.permission;
		this.usesPrefix = false;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);
		if (data == null)
			return;

		if (target.isOnline() && plugin.getPermissionHandler().hasPermission(target.getPlayer(), PermissionType.TROLL_EXEMPT.permission)) {
			err("You may not put &c{0} &4in troll hell!", target.getName());
			data.setTrollHell(false);
			return;
		}

		if (target.isOnline()) {
			Player troll = target.getPlayer();
			if (! data.isTrollHell()) {
				plugin.getTrollHandler().putTrollInHell(troll, TrollType.HELL);
			} else {
				plugin.getTrollHandler().freeFromHell(troll, TrollType.HELL);
			}
		} else {
			data.setTrollHell(! data.isTrollHell());
		}

		String result = FormatUtil.format(plugin.getMessage("troll_hell"), target.getName(), data.isTrollHell() ? "in" : "freed from");
		sendMessage(result);
		plugin.getLogHandler().log(result);
	}

}
