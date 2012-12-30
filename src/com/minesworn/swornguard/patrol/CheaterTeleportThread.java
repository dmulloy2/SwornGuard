package com.minesworn.swornguard.patrol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.util.SThread;

public class CheaterTeleportThread extends SThread {
	final Player player;
	final PlayerInfo i;
	CheaterTeleportThread(Player player, PlayerInfo i) {
		super();
		this.player = player;
		this.i = i;
	}
	
	@Override
	public void run() {
		try {
			int duration = 60;
			while (i.isCheaterInspecting() && duration > 0) {
				Thread.sleep(1000);
				duration--;
				
				if (duration <= 0)
					Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
						public void run() {
							Patrol.returnFromCheatInspecting(player);
						}
					});
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
