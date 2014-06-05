/**
 * (c) 2013 - 2014 dmulloy2
 */
package net.t7seven7t.swornguard.listeners;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.FactionKick;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.Reloadable;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.TimeUtil;
import net.t7seven7t.swornguard.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;

/**
 * @author dmulloy2
 */
public class FactionsListener implements Listener, Reloadable {
	private final SwornGuard plugin;
	private boolean factionBetrayalDetectorEnabled;

	public FactionsListener(final SwornGuard plugin) {
		this.plugin = plugin;
		this.reload();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoinFaction(FPlayerJoinEvent event) {
		FPlayer fplayer = event.getFPlayer();
		if (event.isCancelled() || fplayer == null) {
			return;
		}

		String name = fplayer.getName();
		if (name == null || name.isEmpty()) {
			return;
		}

		OfflinePlayer player = Util.matchOfflinePlayer(name);
		if (player == null) {
			return;
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data == null) {
			return;
		}

		data.setFactions(data.getFactions() + 1);
		data.setLastFaction(event.getFaction().getTag());

		String action = "joined";
		if (event.getReason() == FPlayerJoinEvent.PlayerJoinReason.CREATE) {
			action = "created";
		}

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&e[{0}] &b{1} {2} &e{3}", TimeUtil.getLongDateCurr(), data.getLastKnownBy(), action,
				event.getFaction().getTag()));

		data.getFactionLog().add(line.toString());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeaveFaction(FPlayerLeaveEvent event) {
		FPlayer fplayer = event.getFPlayer();
		if (event.isCancelled() || fplayer == null) {
			return;
		}

		String name = fplayer.getName();
		if (name == null || name.isEmpty()) {
			return;
		}

		OfflinePlayer player = Util.matchOfflinePlayer(name);
		if (player == null) {
			return;
		}

		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data == null) {
			return;
		}

		if (factionBetrayalDetectorEnabled) {
			if (event.getReason() == FPlayerLeaveEvent.PlayerLeaveReason.KICKED) {
				plugin.getFactionBetrayalDetector().addPossibleBetrayedPlayer(data.getLastKnownBy(),
						new FactionKick(event.getFaction().getTag(), System.currentTimeMillis()));
			}
		}

		String action = "left";
		if (event.getReason() == FPlayerLeaveEvent.PlayerLeaveReason.KICKED) {
			action = "was kicked from";
		}

		if (event.getReason() == FPlayerLeaveEvent.PlayerLeaveReason.DISBAND) {
			action += " (disbanded)";
		}

		if (event.getReason() == FPlayerLeaveEvent.PlayerLeaveReason.RESET) {
			action += " (inactivity)";
		}

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&e[{0}] &b{1} {2} &e{3}", TimeUtil.getLongDateCurr(), data.getLastKnownBy(), action,
				event.getFaction().getTag()));

		data.getFactionLog().add(line.toString());
	}

	@Override
	public void reload() {
		this.factionBetrayalDetectorEnabled = plugin.getConfig().getBoolean("factionBetrayalDetectorEnabled");
	}
}