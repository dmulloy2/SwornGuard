/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.dmulloy2.util.FormatUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class PatrolHandler {
	private final SwornGuard plugin;
	private int index = 0;
	private List<String> recentCheaters;
	private Map<String, Integer> taskIDs;
	
	public PatrolHandler(final SwornGuard plugin) {
		this.plugin = plugin;
		this.recentCheaters = new ArrayList<String>();
		this.taskIDs = new HashMap<String, Integer>();
	}
	
	public void patrol(Player player) {
		patrol(player, null);
	}
	
	public void patrol(Player player, Player target) {
		int n = plugin.getServer().getOnlinePlayers().size();
		if (index >= n)
			index = 0;
		
		if (! recentCheaters.isEmpty() && target == null) {
			target = plugin.getServer().getPlayerExact(recentCheaters.get(0));
			recentCheaters.remove(0);
		}
		
		List<Player> online = new ArrayList<>(plugin.getServer().getOnlinePlayers());
		if (target == null) {
			int iteration = 0;
			target = online.get(index);
			while (plugin.getPermissionHandler().hasPermission(target, Permission.CMD_AUTO_PATROL)) {
				index++;
				if (index >= n) {
					index = 0;
					iteration++;
					if (iteration > 1)
						break;
				}
				target = online.get(index);
			}
			
			if (iteration > 1) {
				player.sendMessage(ChatColor.RED + "No players to be patrolled are online.");
				if (plugin.getPlayerDataCache().getData(player).isPatrolling()) {
					unAutoPatrol(player);
				}
				return;
			}
		}
		
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setPatrols(data.getPatrols() + 1);
		
		player.teleport(target);
		player.sendMessage(ChatColor.RED + "You were teleported to " + ChatColor.GOLD + target.getName());
		plugin.getLogHandler().log("{0} was teleported to {1}", player.getName(), target.getName());
		index++;
	}
	
	public void autoPatrol(final Player player, final int interval) {
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		if (data.getPreviousLocation() == null)
			data.setPreviousLocation(player.getLocation());
		
		data.setPatrolling(true);
		data.setPatrolInterval(interval);

		applyPatrolBuffs(player, true);
		plugin.getLogHandler().log(player.getName() + " has started auto patrolling.");
		
		int id = plugin.getServer().getScheduler().runTaskTimer(plugin, new BukkitRunnable() {
			
			@Override
			public void run() {
				if (data.isPatrolling())
					patrol(player);
				else
					this.cancel();
			}
			
		}, 0L, interval * 20L).getTaskId();
		
		cancelTasks(player);
		
		taskIDs.put(player.getName(), id);
	}
	
	public void unAutoPatrol(final Player player) {
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setCooldownPatrolling(true);
		int interval = data.getPatrolInterval();
		player.sendMessage(ChatColor.YELLOW + "Patrol mode will wear off in " + interval + " seconds.");
		int id = plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable() {
			
			@Override
			public void run() {
				if (data.isCooldownPatrolling()) {
					player.teleport(data.getPreviousLocation());
					applyPatrolBuffs(player, false);
					data.setPreviousLocation(null);
					data.setPatrolling(false);
					data.setCooldownPatrolling(false);
				}
			}
			
		}, interval * 20L).getTaskId();
		
		cancelTasks(player);
		
		taskIDs.put(player.getName(), id);
	}
	
	public void unAutoPatrolNoCooldown(Player player) {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		applyPatrolBuffs(player, false);
		player.teleport(data.getPreviousLocation());
		data.setPreviousLocation(null);
		data.setPatrolling(false);
		data.setCooldownPatrolling(false);
		
		cancelTasks(player);
	}
	
	public void cancelTasks(Player player) {
		if (taskIDs.get(player.getName()) != null) {
			plugin.getServer().getScheduler().cancelTask(taskIDs.get(player.getName()));
		}
	}
	
	public void applyPatrolBuffs(Player player, boolean apply) {
		vanish(player, apply);
		
		if (player.getGameMode().equals(GameMode.SURVIVAL)){ 
			player.setAllowFlight(apply);
			player.setFlying(apply);
		}
	}
	
	public void vanish(Player player, boolean vanish) {
		plugin.getPlayerDataCache().getData(player).setVanished(vanish);
		
		if (vanish) {
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (!plugin.getPermissionHandler().hasPermission(p, Permission.VANISH_SPY)) {
					p.hidePlayer(player);
					String msg = plugin.getConfig().getString("quitMessage");
					msg = FormatUtil.format(msg, player.getName());
					p.sendMessage(msg);
				} else {
					p.sendMessage(ChatColor.DARK_GRAY + player.getName() + " is now vanished. Shhh...");
				}
			}
			
			plugin.getLogHandler().log(player.getName() + " is now vanished.");
			player.sendMessage(ChatColor.AQUA + "Vanished!");
		} else {
			for (Player p : plugin.getServer().getOnlinePlayers()) {
				p.showPlayer(player);
				
				if (plugin.getPermissionHandler().hasPermission(p, Permission.VANISH_SPY)) {
					p.sendMessage(ChatColor.DARK_GRAY + player.getName() + " is no longer vanished.");
				} else {
					String msg = plugin.getConfig().getString("joinMessage");
					msg = FormatUtil.format(msg, player.getName());
					p.sendMessage(msg);
				}
			}
			
			plugin.getLogHandler().log(player.getName() + " is no longer vanished.");
			player.sendMessage(ChatColor.AQUA + "Unvanished!");
		}
	}
	
	public void cheaterTeleport(final Player player, final Player target) {
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		if (data.getPreviousLocation() == null)
			data.setPreviousLocation(player.getLocation());
		
		if (data.getReportsRespondedTo() == 0)
			data.setReportsRespondedTo(1);
		else
			data.setReportsRespondedTo(data.getReportsRespondedTo() + 1);
		data.setInspecting(true);
		applyPatrolBuffs(player, true);
//		teleportToGround(player, target);
		player.teleport(target);
		plugin.getLogHandler().log("{0} is now inspecting {1} for cheat report.", player.getName(), target.getName());
		
		int id = plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable() {
			
			@Override
			public void run() {
				if (data.isInspecting())
					returnFromInspecting(player);
			}
			
		}, 60 * 20L).getTaskId();
		
		cancelTasks(player);
		
		taskIDs.put(player.getName(), id);
	}
	
	public void returnFromInspecting(final Player player) {
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		player.teleport(data.getPreviousLocation());
		applyPatrolBuffs(player, false);
		data.setPreviousLocation(null);
		
		int id = plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable() {
			
			@Override
			public void run() {
				data.setInspecting(false);
			}
			
		}, 20L).getTaskId();
		
		cancelTasks(player);
		
		taskIDs.put(player.getName(), id);
	}
	
//	public void teleportToGround(Player player, Player target) {
//		Location loc = target.getLocation();
//		while (target.getWorld().getBlockAt(loc.subtract(0, 1, 0)).getType() == Material.AIR);
//		player.teleport(loc);
//	}
	
	public void addCheater(final String player) {
		recentCheaters.add(player);
		plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable() {
			
			@Override
			public void run() {
				recentCheaters.remove(player);
			}
			
		}, 120 * 20L);
	}
	
	public List<String> getRecentCheaters() {
		return recentCheaters;
	}

}
