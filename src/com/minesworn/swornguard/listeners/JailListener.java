package com.minesworn.swornguard.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.util.Util;
import com.minesworn.swornjail.events.JailEvent;
import com.minesworn.swornjail.events.UnjailEvent;

public class JailListener implements Listener {

	@EventHandler
	public void onJailEvent(JailEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getInmate().getName());
		i.setJailCount(i.getJailCount() + 1);
		i.setLastJailedBy(e.getInmateEntry().getJailer());
		i.setLastJailReason(e.getInmateEntry().getReason());
		i.setLastJailTime(System.currentTimeMillis());
		i.getProfilerList().add("[" + Util.getLongDateCurr() + " GMT]" + ChatColor.DARK_PURPLE + " Jailed by " + e.getInmateEntry().getJailer() + " for: " + e.getInmateEntry().getReason());
	}
	
	@EventHandler
	public void onUnjailEvent(UnjailEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getInmate().getName());
		i.getProfilerList().add("[" + Util.getLongDateCurr() + " GMT]" + ChatColor.DARK_PURPLE + ((e.getUnjailer() != null) ? " Unjailed by " + e.getUnjailer() : ""));
	}
	
}
