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

public class CmdTrollMute extends SwornGuardCommand {
	public CmdTrollMute(SwornGuard plugin) {
		super(plugin);
		this.name = "trollmute";
		this.aliases.add("hellmute");
		this.requiredArgs.add("player");
		this.description = "Temporarily silence a troll";
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
			err("You may not troll mute &c{0}&4!", target.getName());
			return;
		}

		if (target.isOnline()) {
			Player troll = target.getPlayer();
			if (! data.isTrollMuted()) {
				plugin.getTrollHandler().putTrollInHell(troll, TrollType.MUTE);
			} else {
				plugin.getTrollHandler().freeFromHell(troll, TrollType.MUTE);
			}
		} else {
			if (data.isTrollMuted()) {
				data.setTrollMuted(false);
				data.setTrollHell(false);
			} else {
				data.setTrollMuted(true);
				data.setTrollHell(true);
			}
		}

		String result = FormatUtil.format(plugin.getMessage("troll_mute"), target.getName(), data.isTrollHell() ? "muted" : "unmuted");
		sendMessage(result);
		plugin.getLogHandler().log(result);
	}

}
