package com.minesworn.swornguard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import com.minesworn.swornguard.detectors.ChatManager;

public class ServerListener implements Listener {

	@EventHandler
	public void onServerCommand(ServerCommandEvent e) {
		String command = e.getCommand().toLowerCase().split(" ")[0].replace("/", "");
		ChatManager.checkCommand(e.getSender(), command, e.getCommand());
	}
	
}
