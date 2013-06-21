/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.events;

import net.t7seven7t.swornguard.types.CheatType;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author t7seven7t
 */
public class CheatEvent extends Event {
	private static final HandlerList handlers = new HandlerList();
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
	
	public CheatType getCheat() {
		return cheat;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
