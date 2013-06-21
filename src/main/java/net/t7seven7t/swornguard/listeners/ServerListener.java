/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.listeners;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

/**
 * @author t7seven7t
 */
public class ServerListener implements Listener {
	private final SwornGuard plugin;
	
	public ServerListener(SwornGuard plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onServerCommand(ServerCommandEvent event) {
		String command = event.getCommand().toLowerCase().split(" ")[0];
		String[] args = event.getCommand().split(" ");
		if (args.length > 0) {
			List<String> Args = new ArrayList<String>();
			for (int i = 1; i < args.length; i++)
				Args.add(args[i]);
			plugin.getCommandDetector().checkCommand(event.getSender(), command, Args.toArray(new String[0]));
		}
	}
}
