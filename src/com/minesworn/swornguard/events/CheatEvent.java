package com.minesworn.swornguard.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.minesworn.swornguard.Cheat;

public class CheatEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
    private String message;
    private Player player;
    private Cheat type;
 
    public CheatEvent(Player playerName, String message, Cheat type) {
    	this.player = playerName;
        this.message = message;
        this.type = type;
    }
 
    public String getMessage() {
        return message;
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public Cheat getType() {
    	return type;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
	
}
