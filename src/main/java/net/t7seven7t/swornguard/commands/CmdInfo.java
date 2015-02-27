/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.types.StringJoiner;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.ListUtil;
import net.dmulloy2.util.TimeUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdInfo extends SwornGuardCommand {

	public CmdInfo(SwornGuard plugin) {
		super(plugin);
		this.name = "info";
		this.aliases.add("i");
		this.aliases.add("stats");
		this.description = plugin.getMessage("desc_info");
		this.permission = Permission.CMD_INFO;
		this.addOptionalArg("player");
		this.usesPrefix = true;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0, hasPermission(sender, Permission.CMD_INFO_OTHERS));
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);

		List<String> lines = new ArrayList<String>();
		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("info_header"),
					target.getName(),
					target.isOnline()
					? plugin.getMessage("info_online_now")
							: FormatUtil.format(plugin.getMessage("info_last_seen"),
												TimeUtil.formatTimeDifference(	data.getLastOnline(),
																				System.currentTimeMillis()))));
			
		String ip = target.isOnline() ? target.getPlayer().getAddress().getAddress().getHostAddress() :
			data.getIpAddressList().get(data.getIpAddressList().size() - 1);
		if (plugin.getPermissionHandler().hasPermission(sender, Permission.CMD_IP))
			line.append(" from " + ip);

		lines.add(line.toString());

		// Attempt to get the player's previous names
		// We'll try to grab them from Essentials if we don't have a record
		List<String> history = data.getHistory();
		if (history == null || history.isEmpty()) {
			if (plugin.isEssentialsEnabled()) {
				history = plugin.getEssentialsHandler().getHistory(target.getUniqueId());
			} else {
				history = new ArrayList<String>();
				history.add(target.getName());
			}

			data.setHistory(history);
		}

		history = new ArrayList<String>(data.getHistory());
		ListUtil.removeIgnoreCase(history, target.getName());

		if (! history.isEmpty()) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format("&ePreviously known as: &a{0}",
						new StringJoiner("&e, &a").appendAll(history).toString()));
			lines.add(line.toString());
		}

		line = new StringBuilder();
		line.append("  " + FormatUtil.format(plugin.getMessage("info_logins"),
					data.getLogins(),
					TimeUtil.getSimpleDate(target.getFirstPlayed())));
		lines.add(line.toString());
		
		line = new StringBuilder();
		line.append("  " + FormatUtil.format(plugin.getMessage("info_blocks"),
					data.getBlocksBuilt(),
					data.getBlocksDeleted(),
					data.getMessages()));
		lines.add(line.toString());
		
		line = new StringBuilder();
		line.append("  " + FormatUtil.format(plugin.getMessage("info_kills"),
					data.getPlayerKills(),
					data.getMonsterKills(),
					data.getAnimalKills(),
					data.getDeaths()));
		lines.add(line.toString());
		
		if (	plugin.getServer().getPluginManager().isPluginEnabled("Factions") ||
				plugin.getServer().getPluginManager().isPluginEnabled("SwornNations")) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_factions"),
						data.getFactions()));
			if (data.getLastFaction() != "")
				line.append(FormatUtil.format(plugin.getMessage("info_last_faction"),
							data.getLastFaction()));
			lines.add(line.toString());
		}
		
		if (data.getPlayersKicked() != 0 || data.getPlayersBanned() != 0) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_kickban"),
						data.getPlayersKicked(),
						data.getPlayersBanned()));
			lines.add(line.toString());
		}
		
		if (data.getReportsRespondedTo() != 0 || data.getPatrols() != 0) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_cheatrespond"),
						data.getReportsRespondedTo(),
						data.getPatrols()));
			lines.add(line.toString());
		}
		
		if (data.getKicks() != 0) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_kicks"),
						data.getKicks(),
						TimeUtil.formatTimeDifference(data.getLastKick(), System.currentTimeMillis()),
						data.getLastKicker()));
			lines.add(line.toString());
			
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_kick_reason"), data.getLastKickReason()));
			lines.add(line.toString());
		}
		
		if (data.getBans() != 0) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_bans"),
						data.getBans(),
						TimeUtil.formatTimeDifference(data.getLastBan(), System.currentTimeMillis()),
						data.getLastBanner()));
			lines.add(line.toString());
			
			line = new StringBuilder();
			String banreason = data.getLastBanReason();
			if (banreason == null)
				banreason = "The Banhammer has spoken!";
			line.append("  " + FormatUtil.format(plugin.getMessage("info_ban_reason"), banreason));
			lines.add(line.toString());
		}
		
		if (data.getJails() != 0) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_jails"),
						data.getJails(),
						TimeUtil.formatTimeDifference(data.getLastJail(), System.currentTimeMillis()),
						data.getLastJailer()));
			lines.add(line.toString());
			
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_jail_reason"), data.getLastJailReason()));
			lines.add(line.toString());
		}

		if (data.getTrollHells() != 0) {
			line = new StringBuilder();
			line.append("  " + FormatUtil.format(plugin.getMessage("info_trolls"),
					data.getTrollHells(),
					TimeUtil.formatTimeDifference(data.getLastTrollHell(), System.currentTimeMillis()),
					data.getLastTroller()));
			lines.add(line.toString());

			line = new StringBuilder();
			String lastReason = data.getLastTrollReason();
			line.append(" " + FormatUtil.format(plugin.getMessage("info_troll_reason"), lastReason == null ? "not applicable" : lastReason));
			lines.add(line.toString());
		}
		
		if (target.isOnline())
			data.updateSpentTime();
		
		line = new StringBuilder();
		line.append("  " + FormatUtil.format(plugin.getMessage("info_time_spent"), TimeUtil.formatTimeDifference(0, data.getOnlineTime())));
		lines.add(line.toString());
		
		for (String string : lines)
			sendMessage(string);
	}
	
}
