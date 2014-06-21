/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author dmulloy2
 */

public class CmdLeaderboard extends SwornGuardCommand {
	protected boolean updating;
	protected long lastUpdateTime;
	protected List<String> leaderboard;

	public CmdLeaderboard(SwornGuard plugin) {
		super(plugin);
		this.name = "lb";
		this.aliases.add("leaderboard");
		this.description = "Display kills leaderboard";
		this.permission = Permission.CMD_LEADERBOARD;
		this.mustBePlayer = true;
	}

	@Override
	public void perform() {
		if (updating) {
			err("Leaderboard is already updating!");
			return;
		}

		if (leaderboard == null) {
			this.leaderboard = new ArrayList<>();
		}

		if (System.currentTimeMillis() - lastUpdateTime > 600000L) {
			sendMessage(plugin.getMessage("leaderboard_wait"));
			this.updating = true;
			new BuildLeaderboardThread();
		}

		new DisplayLeaderboardThread(sender.getName());
	}

	public void displayLeaderboard(String playerName) {
		Player player = Util.matchPlayer(playerName);
		if (player == null)
			return;

		// Header
		sendMessage(player, plugin.getMessage("leaderboard_header"));

		// Body
		for (String line : leaderboard) {
			sendMessage(player, line);
		}

		// Footer
		sendMessage(player, "&eTo see your most recent stats, type &b/p &3info");
		sendMessage(player, "&eLeaderboard is updated every 10 minutes.");
	}

	public class BuildLeaderboardThread extends Thread {
		public BuildLeaderboardThread() {
			super("SwornGuard-BuildLeaderboard");
			this.setPriority(MIN_PRIORITY);
			this.start();
		}

		@Override
		public void run() {
			plugin.getLogHandler().log("Updating leaderboard...");

			long start = System.currentTimeMillis();

			Map<String, PlayerData> allData = plugin.getPlayerDataCache().getAllPlayerData();
			Map<PlayerData, Integer> experienceMap = new HashMap<>();

			for (Entry<String, PlayerData> entry : allData.entrySet()) {
				PlayerData value = entry.getValue();
				if (value != null && value.getPlayerKills() > 0) {
					experienceMap.put(value, value.getPlayerKills());
				}
			}

			if (experienceMap.isEmpty()) {
				err("No players with kills found!");
				return;
			}

			List<Entry<PlayerData, Integer>> sortedEntries = new ArrayList<>(experienceMap.entrySet());
			Collections.sort(sortedEntries, new Comparator<Entry<PlayerData, Integer>>() {

				@Override
				public int compare(Entry<PlayerData, Integer> entry1, Entry<PlayerData, Integer> entry2) {
					return -entry1.getValue().compareTo(entry2.getValue());
				}

			});

			// Clear the map
			experienceMap.clear();

			// Reinitialize the leaderboard
			leaderboard = new ArrayList<>();

			String format = plugin.getMessage("leaderboard_format");

			for (int i = 0; i < sortedEntries.size() && i < 10; i++) {
				try {
					PlayerData data = sortedEntries.get(i).getKey();

					String space = "";
					String name = data.getLastKnownBy();
					for (int ii = name.length(); ii < 19; ii++)
						space = space + " ";
					name = name + space;

					int kills = data.getPlayerKills();
					int deaths = data.getDeaths();

					leaderboard.add(FormatUtil.format(format, i + 1, name, kills, deaths, formatKDR(kills, deaths)));
				} catch (Throwable ex) {
					continue;
				}
			}

			sortedEntries.clear();

			lastUpdateTime = System.currentTimeMillis();
			updating = false;

			plugin.getLogHandler().log("Leaderboard updated! [{0}ms]", System.currentTimeMillis() - start);

			// Save the data
			plugin.getPlayerDataCache().save();

			// Clean up the data
			new BukkitRunnable() {
				@Override
				public void run() {
					plugin.getPlayerDataCache().cleanupData();
				}
			}.runTaskLater(plugin, 2L);
		}
	}

	public final String formatKDR(int kills, int deaths) {
		if (deaths > 0) {
			DecimalFormat format = new DecimalFormat("#.##");
			return format.format((double) kills / (double) deaths);
		}

		return String.valueOf(kills);
	}

	public class DisplayLeaderboardThread extends Thread {
		private String playerName;

		public DisplayLeaderboardThread(String playerName) {
			super("SwornGuard-DisplayLeaderboard");
			this.setPriority(MIN_PRIORITY);
			this.playerName = playerName;
			this.start();
		}

		@Override
		public void run() {
			try {
				while (updating) {
					sleep(500L);
				}

				displayLeaderboard(playerName);
			} catch (Throwable ex) {
				Player player = Util.matchPlayer(playerName);
				if (player != null)
					sendMessage(player, "&cError: &4Failed to update leaderboard: &c{0}", ex);

				plugin.getLogHandler().log(Level.WARNING, Util.getUsefulStack(ex, "updating leaderboard"));
			}
		}
	}
}