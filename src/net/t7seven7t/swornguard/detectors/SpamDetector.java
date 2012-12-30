/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.tasks.DatableRunnable;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.Util;

/**
 * @author t7seven7t
 */
public class SpamDetector {
	private static final int MESSAGE_DECAY_TIME = 2;
	private final SwornGuard plugin;
	private final Map<String, Map<String, Long>> recentMessages;
	private final int spamThreshold;
	
	public SpamDetector(final SwornGuard plugin) {
		this.plugin = plugin;
		this.recentMessages = new ConcurrentHashMap<String, Map<String, Long>>(100, 0.75f, 4);
		this.spamThreshold = plugin.getConfig().getInt("spamDetectorThresholdPerSecond");
		
		// Cleanup map every 5 mins
		new BukkitRunnable() {
			
			public void run() {
				for (String playerName : Collections.unmodifiableMap(recentMessages).keySet()) {
					if (!Util.matchOfflinePlayer(playerName).isOnline())
						recentMessages.remove(playerName);
				}
			}
			
		}.runTaskTimer(plugin, 6000L, 6000L);
	}
	
	public boolean checkSpam(final Player player, String message, final ChatType type) {
		if (!plugin.getPermissionHandler().hasPermission(player, PermissionType.ALLOW_SPAM.permission)) {
			Map<String, Long> messages = recentMessages.get(player.getName());
			final long now = System.currentTimeMillis();
			
			if (messages == null) {
				messages = new ConcurrentHashMap<String, Long>(16, 0.75f, 4);
				messages.put(message, now);
				recentMessages.put(player.getName(), messages);
				return false;
			}
			
			boolean cancelled = false;
			
			for (Entry<String, Long> entry : Collections.unmodifiableMap(messages).entrySet()) {
				if (now - entry.getValue() > MESSAGE_DECAY_TIME * 1000L)
					messages.remove(entry.getKey());
			}
			
			if (messages.size() >= spamThreshold * MESSAGE_DECAY_TIME) {
				cancelled = true;
				
				new DatableRunnable(player) {
					
					public void run() {
						if (System.currentTimeMillis() - plugin.getPlayerDataCache().getData(player.getName()).getLastSpamWarn() > 2000L) {
							plugin.getPlayerDataCache().getData(player).setLastSpamWarn(System.currentTimeMillis());
							CheatEvent event = new CheatEvent(player.getName(), CheatType.SPAM, FormatUtil.format("[SPAMMER] {0} is trying to spam {1}!", player.getName(), type.toString()));
							plugin.getCheatHandler().announceCheat(event);
						}
					}
					
				}.runTask(plugin);
				messages.clear();
			}
			
			if (!cancelled && compareMessages(message, messages.keySet())) {
				cancelled = true;
			}
			
			messages.put(message, now);
			return cancelled;
		}
			
		return false;
	}
	
	public boolean compareMessages(String message, Set<String> messages) {
		message = message.toLowerCase();
		
		for (String line : messages) {
			line = line.toLowerCase();
			if (message.length() <= 2 && line.length() <= 2)
				return true;
			
			if (message.equals(line))
				return true;
			
			if (line.length() >= 2 && message.startsWith(line.substring(0, 2)))
				return true;
			
			if (message.length() >= 3 && line.length() >= 3) {
				if (message.length() >= 6 && line.length() >= 6) {
					for (int i = 0; i < 3; i++) {
						if (compareStrings(message, line, i))
							return true;
					}
				} else {
					if (compareStrings(message, line, 2))
						return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean compareStrings(String a, String b, int offset) {
		int sublen = (b.length() / 3) * offset;
		String line = b.substring(sublen, sublen + (b.length() / 3));
		
		if (a.matches(".*" + line + ".*"))
			return true;
		
		return false;
	}
	
	public enum ChatType {
		CHAT,
		COMMAND;
	}
	
}
