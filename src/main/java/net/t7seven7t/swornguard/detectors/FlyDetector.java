/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

/**
 * @author t7seven7t
 */
public class FlyDetector {
	private static final BlockFace[] directions = new BlockFace[] { BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST };
	private final SwornGuard plugin;
	private final double suspiciousVelocity;
	private final int suspiciousDistFromGround;
	private final int suspiciousMoveDist;
	
	public FlyDetector(final SwornGuard plugin) {
		this.plugin = plugin;
		this.suspiciousVelocity = plugin.getConfig().getDouble("flyDetectorSuspiciousVelocity");
		this.suspiciousDistFromGround = plugin.getConfig().getInt("flyDetectorSuspiciousDistFromGround");
		this.suspiciousMoveDist = plugin.getConfig().getInt("flyDetectorSuspiciousMoveDist");
		
		new BukkitRunnable() {

			@Override
			public void run() {
				step();
			}
			
		}.runTaskTimer(plugin, 20L, 5L);
	}
	
	private void step() {
		for (final Player player : plugin.getServer().getOnlinePlayers()) {
			if (! plugin.getPermissionHandler().hasPermission(player, PermissionType.ALLOW_FLY.permission)) {
				if (! player.getAllowFlight() && (player.getVelocity().getY() < suspiciousVelocity ||
							(! isInWater(player) && getDistanceToGround(player) >= suspiciousDistFromGround))) {
					if (! isPlayerFallingIntoVoid(player) && ! isPlayerInsideCar(player) && ! player.isInsideVehicle() 
							&& ! isNewPlayerJoin(player) && ! hasRecentlyTeleported(player)) {
						final PlayerData data = plugin.getPlayerDataCache().getData(player);
						final Vector previousLocation = player.getLocation().toVector();
						if (! data.isJailed() && System.currentTimeMillis() - data.getLastFlyWarn() > 45000L) {
							data.setLastFlyWarn(System.currentTimeMillis());

							new BukkitRunnable() {

								@Override
								public void run() {
									checkPlayer(player, previousLocation);
								}
	
							}.runTaskLater(plugin, 5L);
						}
					}
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
			CheatEvent event = new CheatEvent(player.getName(), CheatType.FLYING, 
					FormatUtil.format(plugin.getMessage("cheat_message"), player.getName(), "flying!"));
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

	// dmulloy2 new methods
	public boolean isPlayerFallingIntoVoid(Player player) {
		Location loc = player.getLocation();
		if (loc.getBlockY() < 0) {
			return true;
		}
		
		for (int y = loc.getBlockY(); y >= 0; y--) {
			if (loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getType() != Material.AIR) {
				return false;
			}
		}

		return true;
	}

	public boolean isPlayerInsideCar(Player player) {
		if (player.isInsideVehicle()) {
			Entity ent = player.getVehicle();
			if (ent instanceof Vehicle) {
				Vehicle veh = (Vehicle) ent;
				if (veh instanceof Minecart) {
					Minecart cart = (Minecart) veh;
					Location loc = cart.getLocation();
					
					Material type = loc.getBlock().getType();
					
					return type != Material.POWERED_RAIL && type != Material.RAILS
							&& type != Material.DETECTOR_RAIL && type != Material.ACTIVATOR_RAIL;
				}
			}
		}
		
		return false;
	}
	
	public boolean isNewPlayerJoin(Player player) {
		if (! player.hasPlayedBefore()) {
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			if (data != null) {
				return data.getOnlineTime() < 200L;
			}
			
			return true;
		}
		
		return false;
	}

	public boolean hasRecentlyTeleported(Player player) {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data != null) {
			return System.currentTimeMillis() - data.getLastTeleport() < 60L;
		}

		return false;
	}
	
}
