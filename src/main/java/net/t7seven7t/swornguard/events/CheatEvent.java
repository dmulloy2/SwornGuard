/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.events;

import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.util.Util;

import org.bukkit.OfflinePlayer;
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

	private final OfflinePlayer player;
	private final CheatType cheat;
	private final String message;

	@Deprecated
	public CheatEvent(final String player, final CheatType cheat, final String message) {
		this.player = Util.matchOfflinePlayer(player);
		this.message = message;
		this.cheat = cheat;
	}

	public CheatEvent(final OfflinePlayer player, final CheatType cheat, final String message) {
		this.player = player;
		this.cheat = cheat;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getPlayerName() {
		return player.getName();
	}

	public Player getPlayer() {
		return player.getPlayer();
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
