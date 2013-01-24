/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.detectors.SpamDetector.ChatType;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.tasks.DatableRunnable;
import net.t7seven7t.swornguard.tasks.FireworkRunnable;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class ChatListener implements Listener {
	private final SwornGuard plugin;
	private final List<String> allowedCommandsInJail;
	private final boolean spamDetectorEnabled;
	private final List<String> blockedCommands;
	
	public ChatListener(final SwornGuard plugin) {
		this.plugin = plugin;
		this.allowedCommandsInJail = plugin.getConfig().getStringList("allowedCommandsInJail");
		this.spamDetectorEnabled = plugin.getConfig().getBoolean("spamDetectorEnabled");
		this.blockedCommands = plugin.getConfig().getStringList("blockedCommands");
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChat(final AsyncPlayerChatEvent event) {
		if (spamDetectorEnabled) {
			if (plugin.getSpamDetector().checkSpam(event.getPlayer(), event.getMessage(), ChatType.CHAT)) {
				event.setCancelled(true);
				return;
			}
		}
		
		// TODO: mute? maybe not - seems difficult to implement without a new list
	}
	
	// Needs to be at high because factions cancels event for its colour tags :(
	@EventHandler(priority = EventPriority.HIGH)
	public void onAsyncPlayerChatMonitor(final AsyncPlayerChatEvent event) {
		if (!event.isCancelled()) {
			new DatableRunnable(event.getPlayer()) {
				
				public void run() {
					final PlayerData data = plugin.getPlayerDataCache().getData(player);
					if (data.getMessages() == 0)
						data.setMessages(1);
					else
						data.setMessages(data.getMessages() + 1);
					
					if (data.isJailed())
						data.setLastActivity(System.currentTimeMillis());
				}
				
			}.runTask(plugin);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST) 
	public void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			if (spamDetectorEnabled) {
				if (plugin.getSpamDetector().checkSpam(event.getPlayer(), event.getMessage(), ChatType.CHAT)) {
					event.setCancelled(true);
					return;
				}
			}
			
			if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.ALLOW_BLOCKED_COMMANDS.permission)) {
				for (String command : blockedCommands) {
					if (event.getMessage().matches("/" + command + ".*")) {
						event.setCancelled(true);
						return;
					}
				}
			}
			
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data.isJailed() && 
				!plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.ALLOW_USE_COMMANDS_JAILED.permission) && 
				!event.getMessage().equalsIgnoreCase("/jailstatus")) {
					for (String command : allowedCommandsInJail) {
						if (event.getMessage().matches("/" + command.toLowerCase() + ".*"))
							return;
					}
					
					event.setCancelled(true);
					event.getPlayer().sendMessage(FormatUtil.format(plugin.getMessage("jail_cannot_use_command")));
			}
		}
	}
	
	// Factions why you use such low command preprocess priority? T_T
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocessMonitor(final PlayerCommandPreprocessEvent event) {
		if (!event.isCancelled()) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data.isJailed())
				data.setLastActivity(System.currentTimeMillis());
			
			// Check if command needs to be profiled.
			String command = event.getMessage().toLowerCase().split(" ")[0].replace("/", "");
			String[] args = event.getMessage().split(" ");
			if (args.length > 0) {
				List<String> Args = new ArrayList<String>();
				for (int i = 1; i < args.length; i++)
					Args.add(args[i]);
				plugin.getCommandDetector().checkCommand(event.getPlayer(), command, Args.toArray(new String[0]));
			}
			
			// Just some fun here on... ignore this :)
			if (command.equals("firework") && event.getPlayer().hasPermission("firework")) {
				if (data.isFireworking()) {
					data.setFireworking(false);
				} else {
					data.setFireworking(true);
					new FireworkRunnable(plugin, event.getPlayer()).runTaskTimer(plugin, 5L, 5L);
				}
				event.setCancelled(true);
			} else if (command.equals("creepfun") && event.getPlayer().hasPermission("creepfun") && !creepFunMap.containsKey(event.getPlayer().getName())) {
				final Random random = new Random();
				List<UUID> entityIds = new ArrayList<UUID>(25);
				for (int i = 0; i < 5; i++) {
					final int xOffset = random.nextInt(10 * 2) - 10;
					final int zOffset = random.nextInt(10 * 2) - 10;
					final Location spawnLocation = event.getPlayer().getLocation().add(xOffset, 0, zOffset);
					
					// Make sure entity doesn't spawn inside of blocks or floating in the air
					while (	spawnLocation.add(0, 1, 0).getBlockY() < 250 
							&& spawnLocation.getWorld().getBlockAt(spawnLocation).getType().isSolid() 
							&& spawnLocation.getWorld().getBlockAt(spawnLocation.clone().add(0, 1, 0)).getType().isSolid());
					while ( spawnLocation.subtract(0, 1, 0).getBlockY() > 2
							&& spawnLocation.getWorld().getBlockAt(spawnLocation).getType() == Material.AIR);
					
					final Bat bat = spawnLocation.getWorld().spawn(spawnLocation, Bat.class);
					entityIds.add(bat.getUniqueId());
					Creeper prevCreeper = null;
					for (int j = 0; j < 4; j++) {
						Creeper creeper = spawnLocation.getWorld().spawn(spawnLocation, Creeper.class);
						if (j == 0)
							bat.setPassenger(creeper);
						else
							prevCreeper.setPassenger(creeper);
						creeper.setPowered(true);
						prevCreeper = creeper;
						entityIds.add(creeper.getUniqueId());
					}
					
					new BukkitRunnable() {
						private final Color[] colors = new Color[] {Color.RED, Color.YELLOW, Color.ORANGE, Color.BLUE, Color.NAVY, Color.PURPLE};
						private int color = 0;
						
						public void run() {
							if (bat.isValid() && creepFunMap.containsKey(event.getPlayer().getName())) {
								FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];
								
								FireworkEffect effect = FireworkEffect.builder()
										.with(type)
										.withColor(colors[color])
										.withFade(colors[(color + 1 >= colors.length) ? 0 : color + 1])
										.flicker(random.nextBoolean())
										.trail(random.nextBoolean())
										.build();
			
								Firework firework = bat.getWorld().spawn(bat.getLocation(), Firework.class);
								
								FireworkMeta meta = firework.getFireworkMeta();
								meta.addEffect(effect);
								meta.setPower(2);
								
								firework.setFireworkMeta(meta);
								
								color++;
								if (color >= colors.length)
									color = 0;
							} else {
								this.cancel();
							}
						}
						
					}.runTaskTimer(plugin, 5L, 5L);
				}
				creepFunMap.put(event.getPlayer().getName(), entityIds);
				
				new BukkitRunnable() {
					
					public void run() {
						for (UUID uuid : creepFunMap.get(event.getPlayer().getName())) {
							for (Entity e : event.getPlayer().getWorld().getEntities()) {
								if (e.getUniqueId().equals(uuid)) {
									e.remove();
								}
							}
						}
						
						creepFunMap.remove(event.getPlayer().getName());
					}
					
				}.runTaskLater(plugin, 600L);
			}
		}
	}
	
	private final Map<String, List<UUID>> creepFunMap = new HashMap<String, List<UUID>>();
	
	@EventHandler
	public void onCreepFunDamagedEvent(EntityDamageEvent event) {
		if (!creepFunMap.isEmpty()) {
			for (List<UUID> value : creepFunMap.values()) {
				for (UUID uuid : value) {
					if (event.getEntity().getUniqueId().equals(uuid)) {
						event.setCancelled(true);
						return;
					}	
				}
			}
		}
	}
	
	@EventHandler
	public void onCreepFunPrimeEvent(ExplosionPrimeEvent event) {
		if (!creepFunMap.isEmpty()) {
			for (List<UUID> value : creepFunMap.values()) {
				for (UUID uuid : value) {
					if (event.getEntity().getUniqueId().equals(uuid)) {
						event.setCancelled(true);
						return;
					}	
				}
			}
		}
	}

}
