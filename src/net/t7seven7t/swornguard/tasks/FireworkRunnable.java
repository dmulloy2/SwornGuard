/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.tasks;

import java.util.Random;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class FireworkRunnable extends BukkitRunnable {
	private static final Color[] colors = new Color[] {Color.RED, Color.YELLOW, Color.ORANGE, Color.BLUE, Color.NAVY, Color.PURPLE};
	private final Player player;
	private final PlayerData data;
	private final Random rand = new Random();
	private int color;
	
	public FireworkRunnable(SwornGuard plugin, Player player) {
		this.player = player;
		this.data = plugin.getPlayerDataCache().getData(player);
	}
	
	public void run() {
		if (data.isFireworking() && player.isOnline()) {
			FireworkEffect.Type type;
			if (player.getName().contains("creeper")) {
				type = FireworkEffect.Type.CREEPER;
			} else {
				type = FireworkEffect.Type.values()[rand.nextInt(FireworkEffect.Type.values().length)];
			}
			
			FireworkEffect effect = FireworkEffect.builder()
										.with(type)
										.withColor(colors[color])
										.withFade(colors[(color + 1 >= colors.length) ? 0 : color + 1])
										.flicker(rand.nextBoolean())
										.trail(rand.nextBoolean())
										.build();
			
			Firework firework = player.getWorld().spawn(player.getLocation(), Firework.class);
			
			FireworkMeta meta = firework.getFireworkMeta();
			meta.addEffect(effect);
			meta.setPower(1);
			
			firework.setFireworkMeta(meta);
			
			color++;
			if (color >= colors.length)
				color = 0;
		} else {
			this.cancel();
		}
	}

}
