package com.minesworn.swornguard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.detectors.FactionBetrayalDetector;
import com.minesworn.swornguard.detectors.factionbetrayal.Kick;

public class NationsListener implements Listener {

	@EventHandler
	public void onPlayerJoinFaction(FPlayerJoinEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getFPlayer().getName());
		i.setFactionCount(i.getFactionCount() + 1);
		i.setMostRecentFaction(e.getFaction().getTag());
	}
	
	@EventHandler
	public void onPlayerLeaveFaction(FPlayerLeaveEvent e) {
		if (e.getReason() == FPlayerLeaveEvent.PlayerLeaveReason.KICKED) {
			FactionBetrayalDetector.addPossibleBetrayedPlayer(e.getFPlayer().getPlayer(), 
					new Kick(e.getFaction().getTag(), System.currentTimeMillis()));
		}
	}
		
}
