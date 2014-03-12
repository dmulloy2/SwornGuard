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
import net.t7seven7t.swornguard.util.Util;

import org.bukkit.OfflinePlayer;

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
		this.optionalArgs.add("reason");
		this.description = "Put someone in troll hell ;)";
		this.permission = PermissionType.CMD_TROLL_HELL.permission;
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

		if (data.isTrollHell()) {
			plugin.getTrollHandler().freeFromHell(sender, target, TrollType.HELL);
			return;
		}

		if (target.isOnline() && plugin.getPermissionHandler().hasPermission(target.getPlayer(), PermissionType.TROLL_EXEMPT.permission)) {
			err("You may not put &c{0} &4in troll hell!", target.getName());
			return;
		}

		if (args.length < 2) {
			err("Please specify a valid reason!");
			return;
		}

		String reason = Util.implode(" ", Arrays.copyOfRange(args, 1, args.length));
		plugin.getTrollHandler().putInHell(sender, target, TrollType.HELL, reason);
	}
}