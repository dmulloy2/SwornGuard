/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import net.dmulloy2.commands.Command;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
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
			if (args.length == 1) {
				target = getTarget(args[argIndex], false);
			}
		} else {
			if (args.length == 0) {
				target = player;
			} else if (others) {
				target = getTarget(args[argIndex], false);
			}
		}

		if (target == null) {
			err(plugin.getMessage("error_player_not_found"));
			return null;
		}

		if (getPlayerData(target) == null) {
			err(plugin.getMessage("error_player_not_found"));
			return null; // Return null if they don't have any data
		}

		return target;
	}

	protected OfflinePlayer getTarget(String name, boolean msg) {
		OfflinePlayer target = Util.matchOfflinePlayer(name);
		if (target == null && msg)
			err(plugin.getMessage("error_player_not_found"), name);
		return target;
	}
	
	protected PlayerData getPlayerData(OfflinePlayer target) {
		return plugin.getPlayerDataCache().getData(target);
	}
	
	protected PlayerData getPlayerData(OfflinePlayer target, boolean create) {
		PlayerData data = getPlayerData(target);
		if (data == null && create) {
			data = plugin.getPlayerDataCache().newData(target);
		}

		return data;
	}

	protected boolean argAsBoolean(int arg, boolean def) {
		if (arg > args.length) {
			return def;
		}

		String string = args[arg].toLowerCase();
		return string.startsWith("y") || string.startsWith("t") || string.equals("on");
	}

}
