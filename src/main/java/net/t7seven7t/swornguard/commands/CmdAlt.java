/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdAlt extends SwornGuardCommand {

	public CmdAlt(SwornGuard plugin) {
		super(plugin);
		this.name = "alt";
		this.addRequiredArg("player");
		this.description = "Check for alternate accounts";
		this.permission = Permission.CMD_ALT;
	}

	@Override
	public void perform() {
		OfflinePlayer target = Util.matchOfflinePlayer(args[0]);
		if (target == null) {
			err("Player \"&c{0}&4\" not found!", args[0]);
			return;
		}

		final PlayerData data = plugin.getPlayerDataCache().getData(target);
		if (data == null) {
			err("No data found for &c{0}&4!", target.getName());
			return;
		}

		final String name = data.getLastKnownBy();
		final List<String> matches = new ArrayList<>();

		sendMessage("Building list... Please wait.");

		class AltLookupTask extends BukkitRunnable {

			@Override
			public void run() {
				List<String> ips = data.getIpAddressList();

				Map<String, PlayerData> dataMap = plugin.getPlayerDataCache().getAllPlayerData();
				for (Entry<String, PlayerData> entry : dataMap.entrySet()) {
					PlayerData data = entry.getValue();
					if (checkMatch(data.getIpAddressList(), ips))
						matches.add(data.getLastKnownBy());
				}

				if (matches.contains(name)) {
					matches.remove(name);
				}

				plugin.getPlayerDataCache().save();

				class CleanupTask extends BukkitRunnable {

					@Override
					public void run() {
						if (matches.isEmpty()) {
							err("No alternates found!");
							return;
						}

						sendMessage("&3====[ &ePossible Alts of {0} &3]====", name);

						for (String match : matches) {
							sendMessage("&b - &e{0}", match);
						}

						plugin.getPlayerDataCache().cleanupData();
					}
				}

				new CleanupTask().runTask(plugin);
			}
		}

		new AltLookupTask().runTaskAsynchronously(plugin);
	}

	private static final boolean checkMatch(List<String> list1, List<String> list2) {
		for (String i : list1) {
			for (String ii : list2) {
				if (i.equalsIgnoreCase(ii))
					return true;
			}
		}

		return false;
	}
}