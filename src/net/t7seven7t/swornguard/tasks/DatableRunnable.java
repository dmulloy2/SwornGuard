/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.tasks;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public abstract class DatableRunnable extends BukkitRunnable {
	protected final Player player;
	public DatableRunnable(Player player) {
		this.player = player;
	}
	
	public abstract void run();

}
