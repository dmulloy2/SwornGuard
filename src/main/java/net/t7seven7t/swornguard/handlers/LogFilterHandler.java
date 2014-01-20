/**
 * (c) 2013 - 2014 dmulloy2
 */
package net.t7seven7t.swornguard.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.Reloadable;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

/**
 * @author dmulloy2
 */
public class LogFilterHandler implements java.util.logging.Filter, org.apache.logging.log4j.core.Filter, Reloadable {
	private final SwornGuard plugin;
	private boolean speedDetectorEnabled;
	private List<Pattern> logFilters;

	public LogFilterHandler(SwornGuard plugin) {
		this.plugin = plugin;
		this.reload();
	}

	public final boolean filter(String message) {
		// Do internal checks first
		if (message.contains("moved too quickly!")) {
			String playerName = message.split(" ")[0];
			
			Player player = Util.matchPlayer(playerName);
			if (player != null) {
				if (speedDetectorEnabled) {
					if (! plugin.getPermissionHandler().hasPermission(player, PermissionType.ALLOW_FLY.permission)) {
						if (! player.getAllowFlight()) {
							if (! isPlayerFallingIntoVoid(player) && ! isPlayerInsideCar(player) && ! player.isInsideVehicle() 
									&& ! isNewPlayerJoin(player) && ! hasRecentlyTeleported(player)) {
								PlayerData data = plugin.getPlayerDataCache().getData(player);
								data.setConsecutivePings(data.getConsecutivePings() + 1);
								if (data.getConsecutivePings() >= 2) {
									// Announce the cheat
									CheatEvent event = new CheatEvent(player.getName(), CheatType.SPEED, 
											FormatUtil.format(plugin.getMessage("cheat_message"), player.getName(), "moving too quickly!"));
									plugin.getCheatHandler().announceCheat(event);
									
									// Reset their consecutive pings
									data.setConsecutivePings(0);
								}
							}
						}
					}
				}

				return false;
			}
		}

		// Now filter messages defined in the config
		if (! logFilters.isEmpty()) {
			for (Pattern filter : logFilters) {
				if (filter.matcher(message).matches()) {
					return false;
				}
			}
		}

		return true;
	}

	public final void applyFilters() {
    	plugin.getServer().getLogger().setFilter(this);
    	java.util.logging.Logger.getLogger("Minecraft").setFilter(this);
    	((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(this);
	}

	public final boolean isPlayerFallingIntoVoid(Player player) {
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

	public final boolean isPlayerInsideCar(Player player) {
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
	
	public final boolean isNewPlayerJoin(Player player) {
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
			return (System.currentTimeMillis() - data.getLastTeleport()) > 60L;
		}

		return false;
	}

	@Override
	public void reload() {
		this.speedDetectorEnabled = plugin.getConfig().getBoolean("speedDetectorEnabled", true);
		this.logFilters = new ArrayList<Pattern>();

		if (plugin.getConfig().isSet("log-filters")) {
			for (String string : plugin.getConfig().getStringList("log-filters")) {
				try {
					logFilters.add(Pattern.compile(string));
				} catch (PatternSyntaxException ex) {
					plugin.getLogHandler().log(java.util.logging.Level.WARNING, "Supplied regex filter {0} is invalid! Ignoring!", string);
				}
			}
		}

		this.applyFilters();
	}

	// Default Filter Method

	@Override
	public boolean isLoggable(LogRecord record) {
		return filter(record.getMessage());
	}

	// ---- Log4J Filter Methods ---- //

	@Override
	public Result filter(LogEvent event) {
		return filter(event.getMessage().getFormattedMessage()) ? Result.ACCEPT : Result.DENY;
	}

	@Override
	public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, String message, Object... args) {
		return filter(message) ? Result.ACCEPT : Result.DENY;
	}

	@Override
	public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, Object message, Throwable ex) {
		return filter(message.toString()) ? Result.ACCEPT : Result.DENY;
	}

	@Override
	public Result filter(Logger logger, org.apache.logging.log4j.Level level, Marker marker, Message message, Throwable ex) {
		return filter(message.getFormattedMessage()) ? Result.ACCEPT : Result.DENY;
	}

	@Override
	public Result getOnMatch() {
		return Result.NEUTRAL;
	}

	@Override
	public Result getOnMismatch() {
		return Result.NEUTRAL;
	}
	
}
