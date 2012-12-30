package com.minesworn.swornguard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.events.CheatEvent;
import com.minesworn.swornjail.SwornJail;

public class AutoModerator {

	public static void manageCheatEvent(CheatEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getPlayer().getName());
		String reason = null;
		
		switch (e.getType()) {
		case FLYING:
			reason = Config.autoModeratorkickReasonFlying;
			break;
		case AUTO_ATTACK:
			reason = Config.autoModeratorkickReasonAutoAttack;
			break;
		case COMBAT_LOG:
			i.getProfilerList().add(e.getPlayer().getName() + " pinged the cheat detector for: " + ChatColor.GOLD + e.getType().toString());
			break;
		case KICK_AND_KILL:
			if (SwornGuard.isPluginEnabled("SwornJail"))
				SwornJail.inmatemanager.jail(e.getPlayer(), Config.autoModeratorFactionBetrayalJailTime, "Kick and kill players", "AutoModBot");
			break;
		case SPAM:
			reason = Config.autoModeratorkickReasonSpam;
			break;
		case XRAY:
			if (SwornGuard.isPluginEnabled("SwornJail"))
				SwornJail.inmatemanager.jail(e.getPlayer(), Config.autoModeratorXrayJailTime, "Xray", "AutoModBot");
			reason = Config.autoModeratorkickReasonXray;
			break;
		default:
			break;
		}
		
		if (reason != null) {
			Player player = Bukkit.getPlayerExact(e.getPlayer().getName());
			if (player == null) return;
			player.kickPlayer(reason);
			i.setLastKick("AutoModBot", reason, System.currentTimeMillis());
			SwornGuard.log("Player " + e.getPlayer().getName() + " was kicked by AutoModBot for: " + reason);
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (PermissionsManager.hasPermission(p, Permission.CAN_SEE_CHEAT_REPORTS.node))
					p.sendMessage(ChatColor.YELLOW + "AutoModBot kicked " + e.getPlayer().getName() + " for " + reason);
			}
		}
		
	}
	
	public static boolean isOnlyModeratorOnline() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (PermissionsManager.hasPermission(p, Permission.CAN_SEE_CHEAT_REPORTS.node))
				return Config.autoModeratorAlwaysEnabled;
		}
		return true;
	}
	
}
