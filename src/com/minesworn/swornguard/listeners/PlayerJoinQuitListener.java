package com.minesworn.swornguard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.detectors.CombatLogDetector;
import com.minesworn.swornguard.patrol.Patrol;

public class PlayerJoinQuitListener implements Listener {
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.addPlayer(e.getPlayer().getName());
		long now = System.currentTimeMillis();
		if (i.getFirstOnlineTime() == 0) {
			i.setFirstOnlineTime(now);
		}
		
		i.setLoginCount(i.getLoginCount() + 1);
		String ip = e.getPlayer().getAddress().getAddress().getHostAddress();
		i.setIpAddress(ip);
		if (!i.getIpAddressList().contains(ip))
			i.getIpAddressList().add(ip);
		i.setLastOnlineTime(now);
		i.setOnlineNow(true);
		
		// Hide vanished players from newly joined players.
		if (!e.getPlayer().hasPermission(Permission.CAN_SEE_VANISHED.node))
			for (Player p : Bukkit.getOnlinePlayers())
				if (SwornGuard.playerdatabase.getPlayer(p.getName()).isVanished())
					e.getPlayer().hidePlayer(p);
		
//		for (Entry<String, PlayerInfo> entry : SwornGuard.playerdatabase.getPlayers().entrySet()) {
//			if (entry.getValue().getIpAddressList().contains(ip) && !e.getPlayer().hasPermission(Permission.CAN_CHECK_IPS.node))
//				SwornGuard.warnAltAccount(e.getPlayer().getName(), entry.getKey());
//		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		playerDisconnected(e.getPlayer());
	}
	
	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent e) {
		playerDisconnected(e.getPlayer());
	}
	
	public void playerDisconnected(Player p) {
		if (Config.enableCombatLogDetector)
			CombatLogDetector.check(p);
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
		long now = System.currentTimeMillis();
		i.updateSpentTime();
		i.setLastOnlineTime(now);
		i.setOnlineNow(false);
		
		if (i.isAutoPatrolling())
			Patrol.unAutoPatrolNoCooldown(p);
		if (i.isVanished())
			Patrol.vanish(p, false);
	}
	
}
