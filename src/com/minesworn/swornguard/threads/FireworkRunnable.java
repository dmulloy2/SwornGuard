/**
 * Copyright (C) 2012 t7seven7t
 */
package com.minesworn.swornguard.threads;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class FireworkRunnable extends BukkitRunnable {
	static Color[] colors = new Color[] {Color.RED, Color.YELLOW, Color.ORANGE, Color.GREEN, Color.BLUE, Color.NAVY, Color.PURPLE};

	final Random rand = new Random();
	int color;
	int taskId;
	
	public FireworkRunnable(final int color) {
		this.color = color;
	}
	
	public void run() {
		if (Bukkit.getPlayer("t7seven7t") == null) {
			this.cancel();
			return;
		}
		
		FireworkEffect.Type type = FireworkEffect.Type.values()[rand.nextInt(FireworkEffect.Type.values().length)];
				
		FireworkEffect effect = FireworkEffect.builder().with(type).withColor(colors[color]).withFade(colors[(color + 1 >= colors.length) ? 0 : color + 1]).flicker(rand.nextBoolean()).trail(rand.nextBoolean()).build();
		
		Firework firework = Bukkit.getPlayer("t7seven7t").getWorld().spawn(Bukkit.getPlayer("t7seven7t").getLocation(), Firework.class);
		
		FireworkMeta data = firework.getFireworkMeta();
		data.addEffect(effect);
		data.setPower(1);
		
		firework.setFireworkMeta(data);
		
		color++;
		if (color >= colors.length)
			color = 0;
	}

}
