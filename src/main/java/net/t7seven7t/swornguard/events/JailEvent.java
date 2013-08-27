/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.events;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author t7seven7t
 */
public class JailEvent extends Event implements Cancellable {

	private static final HandlerList handlers = new HandlerList();
	private boolean cancel = false;
	private OfflinePlayer player;
	
	public JailEvent(final OfflinePlayer player) {
		this.player = player;
	}
	
	public OfflinePlayer getPlayer() {
		return player;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}
	
}
