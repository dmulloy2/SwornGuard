/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * @author t7seven7t
 */
public class TagListener implements Listener {
	
	@EventHandler
	public void onNameTag(PlayerReceiveNameTagEvent event) {
		if (event.getNamedPlayer().getName().equals("t7seven7t")) {
			event.setTag(ChatColor.GREEN + "t7seven7t");
		}
	}
	
}
