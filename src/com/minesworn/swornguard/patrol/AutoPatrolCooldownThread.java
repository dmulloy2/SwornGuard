package com.minesworn.swornguard.patrol;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.util.SThread;

public class AutoPatrolCooldownThread extends SThread {
	final Player player;
	final PlayerInfo i;
	AutoPatrolCooldownThread(Player player, PlayerInfo i) {
		super();
		this.player = player;
		this.i = i;
	}

	@Override
	public void run() {
		try {
			while (i.isAutoPatrolling() && System.currentTimeMillis() - i.getStoppedAutoPatrolling() < 30000L)
				Thread.sleep(1000);
			
			Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
				public void run() {
					player.teleport(i.getLocationBeforePatrolling());
					Patrol.applyPatrolBuffs(player, false);
					i.setLocationBeforePatrolling(null);
					i.setAutoPatrolling(false);
					i.setStoppedAutoPatrolling(0);
				}
			});
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
