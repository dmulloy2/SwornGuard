/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.Util;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class SpamDetector {
	private final SwornGuard plugin;
	private final String player;
	
	private final Map<String, Long> messages;
	private final Map<String, Long> commands;
		
	public SpamDetector(final SwornGuard plugin, final Player player) {
		this.plugin = plugin;
		this.messages = new HashMap<String, Long>(16, 0.75f);
		this.commands = new HashMap<String, Long>(16, 0.75f);
		this.player = player.getName();
				
//		// Cleanup map every 5 mins
//		new BukkitRunnable() {
//			
//			public void run() {
//				for (String playerName : Collections.unmodifiableMap(recentMessages).keySet()) {
//					if (!Util.matchOfflinePlayer(playerName).isOnline())
//						recentMessages.remove(playerName);
//				}
//			}
//			
//		}.runTaskTimer(plugin, 6000L, 6000L);
	}
	
	public Map<String, Long> getMessages(final ChatType type) {
		if (type == ChatType.COMMAND)
			return commands;
		
		return messages;
	}
	
	public boolean checkSpam(final String message, final ChatType type) {
		final long now = System.currentTimeMillis();
		
		boolean cancelled = false;
		
		Map<String, Long> messages = getMessages(type);
		
		for (Iterator<Entry<String, Long>> i = messages.entrySet().iterator(); i.hasNext(); ) {
			Entry<String, Long> entry = i.next();
			if (now - entry.getValue() > SpamOptions.MESSAGE_DECAY_TIME * 1000L)
				i.remove();
		}
		
		if (messages.size() >= SpamOptions.SPAM_THRESHOLD * SpamOptions.MESSAGE_DECAY_TIME) {
			cancelled = true;
			
			new BukkitRunnable() {
				
				@Override
				public void run() {
					OfflinePlayer p = Util.matchOfflinePlayer(player);
					PlayerData data = plugin.getPlayerDataCache().getData(p);
					
					if (data == null)
						return;
					
					if (System.currentTimeMillis() - data.getLastSpamWarn() > 2000L) {
						data.setLastSpamWarn(System.currentTimeMillis());
						CheatEvent event = new CheatEvent(p.getName(), CheatType.SPAM, FormatUtil.format("[SPAMMER] {0} is trying to spam {1}!", p.getName(), type.toString()));
						plugin.getCheatHandler().announceCheat(event);
					}
					
				}
				
			}.runTask(plugin);
			
			messages.clear();
		}
		
		if (!cancelled && compareMessages(message, messages.keySet()))
			cancelled = true;
		
		messages.put(message, now);
		return cancelled;
	}
	
	public boolean compareMessages(String message, Set<String> messages) {
		message = message.toLowerCase();
		
		for (String line : messages) {
			line = line.toLowerCase();
			if (SpamOptions.COMPARISON_LEVEL > 0 && message.length() <= 2 && line.length() <= 2)
				return true;
			
			if (SpamOptions.COMPARISON_LEVEL > 1 && message.equals(line))
				return true;
			
			if (SpamOptions.COMPARISON_LEVEL > 2 && line.length() >= 2 && message.startsWith(line.substring(0, 2)))
				return true;
			
			if (SpamOptions.COMPARISON_LEVEL > 3 && message.length() >= 3 && line.length() >= 3) {
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
		return (a.regionMatches(sublen, b, sublen, b.length() / 3));
	}
	
	public enum ChatType {
		CHAT,
		COMMAND;
	}
	
	public static class SpamOptions {
		public static int SPAM_THRESHOLD;
		public static int MESSAGE_DECAY_TIME;
		public static int COMPARISON_LEVEL;
		
		public SpamOptions(final SwornGuard plugin) {
			MESSAGE_DECAY_TIME = plugin.getConfig().getInt("spamDetectorMessageDecayTime");
			SPAM_THRESHOLD = plugin.getConfig().getInt("spamDetectorThresholdPerSecond");
			COMPARISON_LEVEL = plugin.getConfig().getInt("spamDetectorComparisonLevel");
		}
	}
	
}
