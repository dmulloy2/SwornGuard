package com.minesworn.swornguard.patrol;

import org.bukkit.entity.Player;

import com.minesworn.swornguard.core.util.SThread;

public class RemoveRecentCheaterThread extends SThread {
	final Player p;
	RemoveRecentCheaterThread(Player p) {
		this.p = p;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(120 * 1000L);
			Patrol.recentCheaters.remove(p);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
