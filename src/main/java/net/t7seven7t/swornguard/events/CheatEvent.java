/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.events;

import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.util.Util;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author t7seven7t
 */
public class CheatEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	private final String player;
	private final CheatType cheat;
	private final String message;
	
	public CheatEvent(final String player, final CheatType cheat, final String message) {
		this.player = player;
		this.message = message;
		this.cheat = cheat;
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getPlayerName() {
		return player;
	}

	public Player getPlayer() {
		return Util.matchPlayer(player);
	}
	
	public CheatType getCheat() {
		return cheat;
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
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
