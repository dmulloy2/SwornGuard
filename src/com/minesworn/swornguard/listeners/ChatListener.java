package com.minesworn.swornguard.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.PermissionsManager;
import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.detectors.ChatManager;
import com.minesworn.swornguard.threads.FireworkRunnable;

public class ChatListener implements Listener {
	int fireworkRunnableID = -1;

	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerChatEvent(final AsyncPlayerChatEvent e) {
		if (ChatManager.isSpam(e.getPlayer(), e.getMessage(), "messages")) {
			e.setCancelled(true);
			return;
		}
		
		Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
			public void run() {
				PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getPlayer().getName());
				i.setMessageCount(i.getMessageCount() + 1);
			}
		});
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocessEvent (PlayerCommandPreprocessEvent e) {
		if (ChatManager.isSpam(e.getPlayer(), e.getMessage(), "commands")) {
			e.setCancelled(true);
			return;
		}
		
		String command = e.getMessage().toLowerCase().split(" ")[0].replace("/", "");
		
		for (String s : Config.blockedCommands) {
			if (command.equals(s) && !PermissionsManager.hasPermission(e.getPlayer(), Permission.CAN_USE_BLOCKED_CMDS.node)) {
				e.setCancelled(true);				
				return;
			}
		}
				
		ChatManager.checkCommand(e.getPlayer(), command, e.getMessage());
		
		if (command.equalsIgnoreCase("firework") && e.getPlayer().getName().equals("t7seven7t")) {
			if (fireworkRunnableID != 0 && !Bukkit.getScheduler().isCurrentlyRunning(fireworkRunnableID) && !Bukkit.getScheduler().isQueued(fireworkRunnableID)) {
				fireworkRunnableID = Bukkit.getScheduler().runTaskTimer(SwornGuard.p, new FireworkRunnable(0), 10L, 5L).getTaskId();
			} else {
				Bukkit.getScheduler().cancelTask(fireworkRunnableID);
			}
		}
	}
	
}
