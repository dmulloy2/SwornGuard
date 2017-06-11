/**
 * (c) 2013 - 2014 dmulloy2
 */
package net.t7seven7t.swornguard.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import lombok.AllArgsConstructor;
import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.Preconditions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;
import org.bukkit.entity.Player;

/**
 * @author dmulloy2
 */
public class LogFilterHandler extends AbstractFilter implements Filter, Reloadable {
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
		if (message == null) {
			plugin.getLogHandler().log(Level.WARNING, "Encountered a null message!");
			return true; // Probably bukkit's piss-poor way of handling command exceptions
		}

		// Do internal checks first
		if (message.contains("moved too quickly!")) {
			String playerName = message.split(" ")[0];
			
			Player player = Util.matchPlayer(playerName);
			if (player != null) {
				if (speedDetectorEnabled) {
					if (! plugin.getPermissionHandler().hasPermission(player, Permission.ALLOW_FLY)) {
						if (! player.getAllowFlight() && ! player.isInsideVehicle()) {
							if (! preconditions.isPlayerFallingIntoVoid(player) && ! preconditions.isPlayerInsideCar(player)
									&& ! preconditions.isNewPlayerJoin(player) && ! preconditions.hasRecentlyTeleported(player)) {
								PlayerData data = plugin.getPlayerDataCache().getData(player);
								data.setConsecutivePings(data.getConsecutivePings() + 1);
								if (data.getConsecutivePings() >= 2) {
									// Announce the cheat
									CheatEvent event = new CheatEvent(player, CheatType.SPEED,
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
		java.util.logging.Logger logger = plugin.getServer().getLogger();
		Filter current = logger.getFilter();
		if (current == null || current instanceof LogFilterHandler) {
			logger.setFilter(this);
		} else {
			logger.setFilter(new FilterDelegate(current, this));
		}

    	((Logger) LogManager.getRootLogger()).addFilter(this);
	}

	@AllArgsConstructor
	private class FilterDelegate implements Filter {
		private final Filter original;
		private final Filter ours;

		@Override
		public boolean isLoggable(LogRecord record) {
			if (! original.isLoggable(record)) {
				return false;
			}

			return ours.isLoggable(record);
		}
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
					plugin.getLogHandler().log(Level.WARNING, "Supplied regex filter \"{0}\" is invalid!", string);
				}
			}
		}

		if (speedDetectorEnabled && ! logFilters.isEmpty()) {
			this.applyFilters();
		}
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
}
