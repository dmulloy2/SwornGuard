/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.listeners;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.detectors.SpamDetector.ChatType;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.tasks.DatableRunnable;
import net.t7seven7t.swornguard.tasks.FireworkRunnable;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * @author t7seven7t
 */
public class ChatListener implements Listener {
	private final SwornGuard plugin;
	private final List<String> allowedCommandsInJail;
	private final boolean spamDetectorEnabled;
	private final List<String> blockedCommands;
	
	public ChatListener(final SwornGuard plugin) {
		this.plugin = plugin;
		this.allowedCommandsInJail = plugin.getConfig().getStringList("allowedCommandsInJail");
		this.spamDetectorEnabled = plugin.getConfig().getBoolean("spamDetectorEnabled");
		this.blockedCommands = plugin.getConfig().getStringList("blockedCommands");
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if (spamDetectorEnabled) {
			if (plugin.getSpamDetector().checkSpam(event.getPlayer(), event.getMessage(), ChatType.CHAT)) {
				event.setCancelled(true);
				return;
			}
		}
		
		// TODO: mute? maybe not - seems difficult to implement without a new list
	}
	
	// Needs to be at high because factions cancels event for its colour tags :(
	@EventHandler(priority = EventPriority.HIGH)
	public void onAsyncPlayerChatMonitor(final AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			new DatableRunnable(event.getPlayer()) {
				
				public void run() {
					final PlayerData data = plugin.getPlayerDataCache().getData(player);
					if (data.getMessages() == 0)
						data.setMessages(1);
					else
						data.setMessages(data.getMessages() + 1);
					
					if (data.isJailed())
						data.setLastActivity(System.currentTimeMillis());
				}
				
			}.runTask(plugin);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST) 
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			if (spamDetectorEnabled) {
				if (plugin.getSpamDetector().checkSpam(event.getPlayer(), event.getMessage(), ChatType.CHAT)) {
					event.setCancelled(true);
					return;
				}
			}
			
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.ALLOW_BLOCKED_COMMANDS.permission)) {
				for (String command : blockedCommands) {
					if (event.getMessage().matches("/" + command + ".*")) {
						event.setCancelled(true);
						return;
					}
				}
			}
			
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data.isJailed() && 
				!plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.ALLOW_USE_COMMANDS_JAILED.permission) && 
				!event.getMessage().equalsIgnoreCase("/jailstatus")) {
					for (String command : allowedCommandsInJail) {
						if (event.getMessage().matches("/" + command.toLowerCase() + ".*"))
							return;
					}
					
					event.setCancelled(true);
					event.getPlayer().sendMessage(FormatUtil.format(plugin.getMessage("jail_cannot_use_command")));
			}
		}
	}
	
	// Factions why you use such low command preprocess priority? T_T
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocessMonitor(final PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data.isJailed())
				data.setLastActivity(System.currentTimeMillis());
			
			// Check if command needs to be profiled.
			String command = event.getMessage().toLowerCase().split(" ")[0].replace("/", "");
			String[] args = event.getMessage().split(" ");
			if (args.length > 0) {
				List<String> Args = new ArrayList<String>();
				for (int i = 1; i < args.length; i++)
					Args.add(args[i]);
				plugin.getCommandDetector().checkCommand(event.getPlayer(), command, Args.toArray(new String[0]));
			}
			
			// Just some fun here on... ignore this :)
			if (command.equals("firework") && event.getPlayer().hasPermission("firework")) {
				if (data.isFireworking()) {
					data.setFireworking(false);
				} else {
					data.setFireworking(true);
					new FireworkRunnable(plugin, event.getPlayer()).runTaskTimer(plugin, 5L, 5L);
				}
			}
		}
	}
	
	

}
