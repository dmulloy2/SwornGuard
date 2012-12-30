package com.minesworn.swornguard.threads;

import org.bukkit.Bukkit;

import com.minesworn.swornguard.SwornGuard;

public class SaveRunnable extends SGThread {
	
	@Override
	public void run() {
		try {
			while (SwornGuard.p.isEnabled()) {
				Thread.sleep(600000);
				Bukkit.getScheduler().scheduleAsyncDelayedTask(SwornGuard.p, new Runnable() {

					@Override
					public void run() {
						SwornGuard.playerdatabase.save();
					}
					
				});
			}
		} catch (InterruptedException e) {}
	}

}
