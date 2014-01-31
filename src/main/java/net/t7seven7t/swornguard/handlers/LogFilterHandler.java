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
import net.t7seven7t.swornguard.types.Preconditions;
import net.t7seven7t.swornguard.types.Reloadable;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
public class LogFilterHandler implements java.util.logging.Filter, org.apache.logging.log4j.core.Filter, Reloadable {
	private final SwornGuard plugin;
	private final Preconditions preconditions;
	private boolean speedDetectorEnabled;
	private List<Pattern> logFilters;

	public LogFilterHandler(SwornGuard plugin) {
		this.plugin = plugin;
		this.preconditions = plugin.getPreconditions();
		this.reload();
	}

	private final boolean filter(String message) {
		// Do internal checks first
		if (message.contains("moved too quickly!")) {
			String playerName = message.split(" ")[0];
			
			Player player = Util.matchPlayer(playerName);
			if (player != null) {
				if (speedDetectorEnabled) {
					if (! plugin.getPermissionHandler().hasPermission(player, PermissionType.ALLOW_FLY.permission)) {
						if (! player.getAllowFlight() && ! player.isInsideVehicle()) {
							if (! preconditions.isPlayerFallingIntoVoid(player) && ! preconditions.isPlayerInsideCar(player) 
									&& ! preconditions.isNewPlayerJoin(player) && ! preconditions.hasRecentlyTeleported(player)) {
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

	private final void applyFilters() {
    	plugin.getServer().getLogger().setFilter(this);
    	java.util.logging.Logger.getLogger("Minecraft").setFilter(this);
    	((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(this);
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
