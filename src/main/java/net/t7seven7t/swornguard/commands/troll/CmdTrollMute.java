/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands.troll;

import java.util.Arrays;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.TrollType;
import net.t7seven7t.swornguard.util.FormatUtil;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */

public class CmdTrollMute extends SwornGuardCommand {
	public CmdTrollMute(SwornGuard plugin) {
		super(plugin);
		this.name = "trollmute";
		this.aliases.add("hellmute");
		this.requiredArgs.add("player");
		this.optionalArgs.add("reason");
		this.description = "Temporarily silence a troll";
		this.permission = PermissionType.CMD_TROLL_MUTE.permission;
		this.usesPrefix = false;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;

		PlayerData data = getPlayerData(target, true);
		if (data == null)
			return;

		if (data.isTrollMuted()) {
			plugin.getTrollHandler().freeFromHell(sender, target, TrollType.MUTE);
			return;
		}

		if (target.isOnline() && plugin.getPermissionHandler().hasPermission(target.getPlayer(), PermissionType.TROLL_EXEMPT.permission)) {
			err("You may not troll mute &c{0}&4!", target.getName());
			return;
		}

		if (args.length < 2) {
			err("Please specify a valid reason!");
			return;
		}

		String reason = FormatUtil.join(" ", Arrays.copyOfRange(args, 1, args.length));
		plugin.getTrollHandler().putInHell(sender, target, TrollType.MUTE, reason);
	}
}