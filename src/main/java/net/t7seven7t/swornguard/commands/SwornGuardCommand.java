package net.t7seven7t.swornguard.commands;

import net.dmulloy2.commands.Command;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */
public abstract class SwornGuardCommand extends Command {
	protected final SwornGuard plugin;

	public SwornGuardCommand(SwornGuard plugin) {
		super(plugin);
		this.plugin = plugin;
	}

	protected OfflinePlayer getTarget(int argIndex) {
		return getTarget(argIndex, true);
	}

	protected OfflinePlayer getTarget(int argIndex, boolean others) {
		OfflinePlayer target = null;

		if (! isPlayer()) {
			if (args.length > argIndex) {
				target = getTarget(args[argIndex], false);
			}
		} else {
			if (args.length > argIndex && others) {
				target = getTarget(args[argIndex], false);
			} else {
				target = player;
			}
		}

		if (target == null) {
			err(plugin.getMessage("error_player_not_found"));
			return null;
		}

		return target;
	}

	protected OfflinePlayer getTarget(String identifier, boolean msg) {
		OfflinePlayer target = Util.matchOfflinePlayer(identifier);
		if (target == null && msg)
			err(plugin.getMessage("error_player_not_found"), identifier);
		return target;
	}

	protected PlayerData getPlayerData(OfflinePlayer target) {
		return plugin.getPlayerDataCache().getData(target);
	}

	protected PlayerData getPlayerData(OfflinePlayer target, boolean create) {
		PlayerData data = getPlayerData(target);
		if (data == null && create)
			data = plugin.getPlayerDataCache().newData(target);
		return data;
	}

}
