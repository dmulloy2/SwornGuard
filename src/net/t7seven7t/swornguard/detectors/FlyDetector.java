/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;

/**
 * @author t7seven7t
 */
public class FlyDetector {
	private static final BlockFace[] directions = new BlockFace[] {BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
	private final SwornGuard plugin;
	private final double suspiciousVelocity;
	private final int suspiciousDistFromGround;
	private final int suspiciousMoveDist;
	
	public FlyDetector(final SwornGuard plugin) {
		this.plugin = plugin;
		this.suspiciousVelocity = plugin.getConfig().getDouble("flyDetectorSuspiciousVelocity");
		this.suspiciousDistFromGround = plugin.getConfig().getInt("flyDetectorSuspiciousDistFromGround");
		this.suspiciousMoveDist = plugin.getConfig().getInt("flyDetectorSuspiciousMoveDist");
		
		plugin.getServer().getScheduler().runTaskTimer(plugin, new BukkitRunnable() {
			
			public void run() {
				step();
			}
			
		}, 20L, 5L);
	}
	
	private void step() {
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
			if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.ALLOW_FLY.permission) 
					&& !player.getAllowFlight() && (player.getVelocity().getY() < suspiciousVelocity ||
							(!isInWater(player) && getDistanceToGround(player) >= suspiciousDistFromGround))) {
				final PlayerData data = plugin.getPlayerDataCache().getData(player);
				final Vector previousLocation = player.getLocation().toVector();
				if (!data.isJailed() && System.currentTimeMillis() - data.getLastFlyWarn() > 45000L) {
					data.setLastFlyWarn(System.currentTimeMillis());
					plugin.getServer().getScheduler().runTaskLater(plugin, new BukkitRunnable() {
						
						public void run() {
							checkPlayer(player, previousLocation);
						}
						
					}, 5L);
				}
			}
		}
	}

	private void checkPlayer(Player player, Vector previousLocation) {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		
		if (player.getLocation().getY() - previousLocation.getY() > 3 ||
				(player.getLocation().getY() >= previousLocation.getY() && 
					(Math.abs(player.getLocation().getX() - previousLocation.getX()) > suspiciousMoveDist ||
					Math.abs(player.getLocation().getZ() - previousLocation.getZ()) > suspiciousMoveDist))) {
			CheatEvent event = new CheatEvent(player.getName(), CheatType.FLYING, FormatUtil.format(plugin.getMessage("cheat_message"), player.getName(), "flying!"));
			plugin.getCheatHandler().announceCheat(event);
			data.setLastFlyWarn(System.currentTimeMillis());
		} else {
			data.setLastFlyWarn(0);
		}
	}
	
	private boolean isInWater(Player player) {
		Block block = player.getWorld().getBlockAt(player.getLocation());
		
		if (isBlockWaterOrClimbable(block))
			return true;
		
		for (BlockFace face : directions) {			
			if (isBlockWaterOrClimbable(block.getRelative(face)))
				return true;
		}
		
		return false;
	}
	
	public boolean isBlockWaterOrClimbable(Block block) {
		return block != null && (block.isLiquid() || block.getType().equals(Material.LADDER) || block.getType().equals(Material.VINE));
	}
	
	private int getDistanceToGround(Player player) {
		Location loc = player.getLocation();
		int count = 1;
		while (loc.subtract(0, 1, 0).getY() > 1 && count < suspiciousDistFromGround &&
				player.getWorld().getBlockAt(loc).getType().equals(Material.AIR) ||
				player.getWorld().getBlockAt(loc).getType().equals(Material.WATER) ||
				player.getWorld().getBlockAt(loc).getType().equals(Material.LAVA)) {
			count++;
		}
		
		return count;
	}
	
}
