package com.minesworn.swornguard.patrol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.util.SThread;

public class AutoPatrolThread extends SThread {
	final Player player;
	final PlayerInfo i;
	final int interval;
	AutoPatrolThread(Player player, PlayerInfo i, int interval) {
		super();
		this.player = player;
		this.i = i;
		this.interval = interval;
	}
	
	@Override
	public void run() {
		try {
			int x = 0;
			while (i.isAutoPatrolling() && i.getStoppedAutoPatrolling() == 0) {
				Thread.sleep(1000);
				x++;
				
				if (x > interval) {
					x = 0;
					Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
						public void run() {
							Patrol.patrol(player);
						}
					});
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
