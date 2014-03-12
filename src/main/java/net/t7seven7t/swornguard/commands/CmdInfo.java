/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.TimeUtil;

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
		this.permission = PermissionType.CMD_INFO.permission;
		this.optionalArgs.add("player");
		this.usesPrefix = true;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0, hasPermission(sender, PermissionType.CMD_INFO_OTHERS.permission));
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
		if (plugin.getPermissionHandler().hasPermission(sender, PermissionType.CMD_IP.permission))
			line.append(" from " + ip);

		lines.add(line.toString());
		
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
			line.append(" " + FormatUtil.format(plugin.getMessage("info_troll_reason"), data.getLastTrollReason()));
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
