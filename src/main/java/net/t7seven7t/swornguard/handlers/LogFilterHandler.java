/**
 * (c) 2013 dmulloy2
 */
package net.t7seven7t.swornguard.handlers;

import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.Util;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

/**
 * @author dmulloy2
 */

public class LogFilterHandler implements Filter {
	private final SwornGuard plugin;
	private final boolean wrongMovementDetectorEnabled;
	private final List<String> filterMessagesFromLog;

	public LogFilterHandler(SwornGuard plugin) {
		this.plugin = plugin;

		this.wrongMovementDetectorEnabled = plugin.getConfig().getBoolean("wrongMovementDetectorEnabled");
		this.filterMessagesFromLog = plugin.getConfig().getStringList("filterMessagesFromLog");
	}

	@Override
	public boolean isLoggable(LogRecord record) {
		String message = record.getMessage();

		// Do internal checks first
		if (message.contains("moved too quickly!")) {
			String playerName = message.split(" ")[0];
			
			Player player = Util.matchPlayer(playerName);
			if (player != null) {
				if (wrongMovementDetectorEnabled) {
					if (! plugin.getPermissionHandler().hasPermission(player, PermissionType.ALLOW_FLY.permission)) {
						if (! player.getAllowFlight()) {
							if (! isPlayerFallingIntoVoid(player) && ! isPlayerInsideCar(player) && ! player.isInsideVehicle() 
									&& ! isNewPlayerJoin(player) && ! hasRecentlyTeleported(player)) {
								CheatEvent event = new CheatEvent(player.getName(), CheatType.MOVED_WRONGLY, 
										FormatUtil.format(plugin.getMessage("cheat_message"), player.getName(), "moving too quickly!"));
								plugin.getCheatHandler().announceCheat(event);
							}
						}
					}
				}

				return false;
			}
		}

		// Always show severe exceptions
		if (record.getLevel() == Level.SEVERE) {
			return true;
		}

		// Now filter messages defined in the config
		for (String filterMessage : filterMessagesFromLog) {
			if (message.contains(filterMessage)) {
				return false;
			}
		}

		return true;
	}

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
			return System.currentTimeMillis() - data.getLastTeleport() > 60L;
		}

		return false;
	}
	
}
