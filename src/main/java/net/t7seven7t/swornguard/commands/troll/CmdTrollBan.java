/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands.troll;

import java.util.Arrays;

import net.dmulloy2.util.FormatUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.TrollType;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */
public class CmdTrollBan extends SwornGuardCommand {

	public CmdTrollBan(SwornGuard plugin) {
		super(plugin);
		this.name = "trollban";
		this.aliases.add("hellban");
		this.addRequiredArg("player");
		this.addOptionalArg("reason");
		this.description = "Permanently silence a troll";
		this.permission = Permission.CMD_TROLL_BAN;
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

		if (data.isTrollBanned()) {
			plugin.getTrollHandler().freeFromHell(sender, data, target, TrollType.BAN);
			return;
		}

		if (target.isOnline() && plugin.getPermissionHandler().hasPermission(target.getPlayer(), Permission.TROLL_EXEMPT)) {
			err("You may not troll ban &c{0}&4!", target.getName());
			return;
		}

		if (args.length < 2) {
			err("Please specify a valid reason!");
			return;
		}

		String reason = FormatUtil.join(Arrays.copyOfRange(args, 1, args.length));
		plugin.getTrollHandler().putInHell(sender, data, target, TrollType.BAN, reason);
	}
}