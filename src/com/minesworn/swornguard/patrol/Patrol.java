package com.minesworn.swornguard.patrol;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.PermissionsManager.Permission;

public class Patrol {
	
	volatile static int index = 0;
	public volatile static List<Player> recentCheaters = new ArrayList<Player>();
	
	public static void patrol(Player player) {
		int n = Bukkit.getOnlinePlayers().length;
		if (index >= n)
			index = 0;
		
		Player target;

		if (!recentCheaters.isEmpty()) {
			target = recentCheaters.get(0);
			recentCheaters.remove(0);
		} else {
			int iteration = 0;
			target = Bukkit.getOnlinePlayers()[index];
			while (target.hasPermission(Permission.NOT_PATROLLED.node)) {
				index++;
				if (index >= n) {
					index = 0;
					iteration++;
					if (iteration > 1)
						break;
				}
				target = Bukkit.getOnlinePlayers()[index];
			}
			
			if (iteration > 1) {
				player.sendMessage(ChatColor.RED + "No players to be patrolled are online.");
				if (SwornGuard.playerdatabase.getPlayer(player.getName()).isAutoPatrolling())
					unAutoPatrol(player);
			}
		}
		
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		i.setNumTimesPatrolled(i.getNumTimesPatrolled() + 1);
		
		player.teleport(target);
		player.sendMessage(ChatColor.RED + "You were teleported to " + ChatColor.GOLD + target.getName());
		SwornGuard.log(player.getName() + " was teleported to " + target.getName());
		index++;
	}
	
	public static void autoPatrol(Player player, int interval) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		
		if (i.getLocationBeforePatrolling() == null)
			i.setLocationBeforePatrolling(player.getLocation());
		i.setAutoPatrolling(true);
		applyPatrolBuffs(player, true);
		SwornGuard.log(player.getName() + " has started auto patrolling.");
		
		patrol(player);
		new AutoPatrolThread(player, i, interval);
	}
	
	public static void unAutoPatrol(Player player) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		i.setStoppedAutoPatrolling(System.currentTimeMillis());
		player.sendMessage(ChatColor.YELLOW	+ "Patrol mode will wear off in 30 seconds.");
		new AutoPatrolCooldownThread(player, i);
	}
	
	public static void unAutoPatrolNoCooldown(Player player) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		Patrol.applyPatrolBuffs(player, false);
		player.teleport(i.getLocationBeforePatrolling());
		i.setLocationBeforePatrolling(null);
		i.setAutoPatrolling(false);
	}
	
	public static void applyPatrolBuffs(Player player, boolean apply) {
		vanish(player, apply);

		if (player.getGameMode().equals(GameMode.SURVIVAL)){ 
			player.setAllowFlight(apply);
			player.setFlying(apply);
		}
	}
	
	public static void vanish(Player player, boolean vanish) {
		SwornGuard.playerdatabase.getPlayer(player.getName()).setVanished(vanish);

		if (vanish) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p.hasPermission(Permission.CAN_SEE_VANISHED.node) && Config.vanishedPlayersAreVisibleToAdmins)
					p.sendMessage(ChatColor.DARK_GRAY + player.getName() + " is now vanished. Shhh...");
				else {
					p.hidePlayer(player);
					p.sendMessage(ChatColor.YELLOW + player.getName() + " left the game.");
				}
			}
			
			SwornGuard.log(player.getName() + " is now vanished.");
			player.sendMessage(ChatColor.AQUA + "Vanished!");
		} else {
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(player);
				
				if (p.hasPermission(Permission.CAN_SEE_VANISHED.node))
					p.sendMessage(ChatColor.DARK_GRAY + player.getName() + " is no longer vanished.");
				else
					p.sendMessage(ChatColor.YELLOW + player.getName() + " joined the game.");
			}
			
			SwornGuard.log(player.getName() + " is no longer vanished.");
			player.sendMessage(ChatColor.AQUA + "Unvanished!");
		}
	}
	
	public static void cheaterTeleport(Player player, Player target) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		
		if (i.getLocationBeforePatrolling() == null)
			i.setLocationBeforePatrolling(player.getLocation());
		
		i.setNumCheatReportsRespondedTo(i.getNumCheatReportsRespondedTo() + 1);
		i.setCheaterInspecting(true);
		applyPatrolBuffs(player, true);
		teleportToGround(player, target);
		SwornGuard.log(player.getName() + " is now inspecting " + target.getName() + " for cheat report.");
		new CheaterTeleportThread(player, i);		
	}
	
	public static void returnFromCheatInspecting(Player player) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(player.getName());
		player.teleport(i.getLocationBeforePatrolling());
		Patrol.applyPatrolBuffs(player, false);
		i.setLocationBeforePatrolling(null);
		i.setCheaterInspecting(false);
	}

	public static void teleportToGround(Player player, Player target) {
		Location l = target.getLocation();
		while (target.getWorld().getBlockAt(l.subtract(0, 1, 0)).getType() == Material.AIR);
		player.teleport(l);
	}
	
	public static void addCheater(Player p) {
		recentCheaters.add(p);
		new RemoveRecentCheaterThread(p);		
	}
	
}
