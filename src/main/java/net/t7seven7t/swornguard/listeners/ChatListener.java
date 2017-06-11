/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import net.dmulloy2.types.Reloadable;
import net.dmulloy2.util.FormatUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.detectors.SpamDetector;
import net.t7seven7t.swornguard.detectors.SpamDetector.ChatType;
import net.t7seven7t.swornguard.tasks.FireworkRunnable;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
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
public class ChatListener implements Listener, Reloadable {
	private final SwornGuard plugin;
	private List<String> allowedCommandsInJail;
	private boolean spamDetectorEnabled;
	private List<String> blockedCommands;
	private List<String> blockedCommandsInHell;
	private List<String> trollOverrideCommands;
	private boolean simulateMessages;
	private List<String> messageCommands;
	private String fakeMessageFormat;

	public ChatListener(SwornGuard plugin) {
		this.plugin = plugin;
		this.reload();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());

		if (spamDetectorEnabled) {
			if (! plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.ALLOW_SPAM)) {
				if (data.getSpamManager() == null) {
					data.setSpamManager(new SpamDetector(plugin, event.getPlayer()));
				}

				if (data.getSpamManager().checkSpam(event.getMessage(), ChatType.CHAT)) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}

	// Needs to be at high because factions cancels event for its colour tags :(
	@EventHandler(priority = EventPriority.HIGH)
	public void onAsyncPlayerChatMonitor(AsyncPlayerChatEvent event) {
		if (! event.isCancelled()) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data.getMessages() == 0) {
				data.setMessages(1);
			} else {
				data.setMessages(data.getMessages() + 1);
			}

			if (data.isJailed()) {
				data.setLastActivity(System.currentTimeMillis());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if (! event.isCancelled()) {
			Player player = event.getPlayer();
			PlayerData data = plugin.getPlayerDataCache().getData(player);

			if (spamDetectorEnabled) {
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.ALLOW_SPAM)) {
					if (data.getSpamManager() == null) {
						data.setSpamManager(new SpamDetector(plugin, player));
					}

					if (data.getSpamManager().checkSpam(event.getMessage(), ChatType.COMMAND)) {
						event.setCancelled(true);
						return;
					}
				}
			}

			// Lowercase without the slash
			String message = event.getMessage().toLowerCase().substring(1);
			String[] args = event.getMessage().split(" ");
			String label = args[0];

			if (! plugin.getPermissionHandler().hasPermission(player, Permission.ALLOW_BLOCKED_COMMANDS)) {
				for (String command : blockedCommands) {
					if (message.matches(command.toLowerCase() + ".*")) {
						event.setCancelled(true);
						return;
					}
				}
			}

			if (data.isTrollHell()) {
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.ALLOW_USE_COMMANDS_HELL)) {
					if (simulateMessages) {
						for (String command : messageCommands) {
							if (message.matches(command + ".*")) {
								try {
									// This accounts for /r and /reply, but doesn't count whispe[r]
									int beginIndex = label.contains("r") && ! label.contains("w") ? 1 : 2;
									String content = FormatUtil.join(" ", Arrays.copyOfRange(args, beginIndex, args.length));
									String fakeMsg = FormatUtil.format(fakeMessageFormat, player.getDisplayName(), content);
									player.sendMessage(fakeMsg);
								} catch (Throwable ex) {
									// Doesn't matter...
								}

								event.setCancelled(true);
								return;
							}
						}
					}

					for (String command : blockedCommandsInHell) {
						if (message.matches(command + ".*")) {
							event.setCancelled(true);
							return;
						}
					}
				}
			}

			if (trollOverrideCommands != null && ! trollOverrideCommands.isEmpty()) {
				for (String command : trollOverrideCommands) {
					if (message.matches(command + ".*")) {
						try {
							String[] newArgs = Arrays.copyOf(args, args.length);
							newArgs[0] = "/troll";

							player.chat(FormatUtil.join(" ", newArgs));
							event.setCancelled(true);
						} catch (Throwable ex) {
							// Doesn't matter...
						}
					}
				}
			}

			if (data.isJailed()) {
				if (! plugin.getPermissionHandler().hasPermission(player, Permission.ALLOW_USE_COMMANDS_JAILED)) {
					if (! event.getMessage().equalsIgnoreCase("/jailstatus")) {
						for (String command : allowedCommandsInJail) {
							if (message.matches(command + ".*")) {
								return;
							}
						}

						event.setCancelled(true);
						player.sendMessage(FormatUtil.format(plugin.getMessage("jail_cannot_use_command")));
					}
				}
			}
		}
	}

	// Factions why you use such low command preprocess priority? T_T
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerCommandPreprocessMonitor(final PlayerCommandPreprocessEvent event) {
		if (! event.isCancelled()) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data.isJailed())
				data.setLastActivity(System.currentTimeMillis());

			// Check if command needs to be profiled.
			String command = event.getMessage().toLowerCase().split(" ")[0].replace("/", "");
			String[] args = event.getMessage().split(" ");
			if (args.length > 0) {
				plugin.getCommandDetector().checkCommand(event.getPlayer(), command, Arrays.copyOfRange(args, 1, args.length));
			}

			// Just some fun here on... ignore this :)
			if (command.equals("firework")) {
				if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.FIREWORK)) {
					if (data.isFireworking()) {
						data.setFireworking(false);
					} else {
						data.setFireworking(true);
						new FireworkRunnable(plugin, event.getPlayer()).runTaskTimer(plugin, 5L, 5L);
					}
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to do this!");
				}

				event.setCancelled(true);
			} else if (command.equals("creepfun")) {
				if (plugin.getPermissionHandler().hasPermission(event.getPlayer(), Permission.CREEPFUN)) {
					if (! creepFunMap.containsKey(event.getPlayer().getName())) {
						final Random random = new Random();
						List<UUID> entityIds = new ArrayList<UUID>(25);
						for (int i = 0; i < 5; i++) {
							int xOffset = random.nextInt(10 * 2) - 10;
							int zOffset = random.nextInt(10 * 2) - 10;
							Location spawnLocation = event.getPlayer().getLocation().add(xOffset, 0, zOffset);

							// Make sure entity doesn't spawn inside of blocks r floating in the air
							while (spawnLocation.add(0, 1, 0).getBlockY() < 250
									&& spawnLocation.getWorld().getBlockAt(spawnLocation).getType().isSolid()
									&& spawnLocation.getWorld().getBlockAt(spawnLocation.clone().add(0, 1, 0)).getType().isSolid())
								;
							while (spawnLocation.subtract(0, 1, 0).getBlockY() > 2
									&& spawnLocation.getWorld().getBlockAt(spawnLocation).getType() == Material.AIR)
								;

							final Bat bat = spawnLocation.getWorld().spawn(spawnLocation, Bat.class);
							entityIds.add(bat.getUniqueId());
							Creeper prevCreeper = null;
							for (int j = 0; j < 4; j++) {
								Creeper creeper = spawnLocation.getWorld().spawn(spawnLocation, Creeper.class);
								if (j == 0)
									bat.addPassenger(creeper);
								else
									prevCreeper.addPassenger(creeper);
								creeper.setPowered(true);
								prevCreeper = creeper;
								entityIds.add(creeper.getUniqueId());
							}

							new BukkitRunnable() {
								private Color[] colors = new Color[] {
										Color.RED, Color.YELLOW, Color.ORANGE, Color.BLUE, Color.NAVY, Color.PURPLE
								};
								private int color = 0;

								@Override
								public void run() {
									if (bat.isValid() && creepFunMap.containsKey(event.getPlayer().getName())) {
										FireworkEffect.Type type = FireworkEffect.Type.values()[random.nextInt(FireworkEffect.Type.values().length)];

										FireworkEffect effect = FireworkEffect.builder()
												.with(type)
												.withColor(colors[color])
												.withFade(colors[(color + 1 >= colors.length) ? 0 : color + 1])
												.flicker(random.nextBoolean())
												.trail(random.nextBoolean()).build();

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

							@Override
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
				} else {
					event.getPlayer().sendMessage(ChatColor.RED + "You do not have permission to do this!");
				}

				event.setCancelled(true);
			}
		}
	}

	private Map<String, List<UUID>> creepFunMap = new HashMap<String, List<UUID>>();

	@EventHandler
	public void onCreepFunDamagedEvent(EntityDamageEvent event) {
		if (! creepFunMap.isEmpty()) {
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
		if (! creepFunMap.isEmpty()) {
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

	@Override
	public void reload() {
		this.allowedCommandsInJail = formatCommandList("allowedCommandsInJail");
		this.spamDetectorEnabled = plugin.getConfig().getBoolean("spamDetectorEnabled");
		this.blockedCommands = plugin.getConfig().getStringList("blockedCommands");
		this.blockedCommandsInHell = formatCommandList("trollHell.blockedCommands");
		this.trollOverrideCommands = formatCommandList("trollHell.overrideCommands");
		this.simulateMessages = plugin.getConfig().getBoolean("trollHell.simulateMessages");
		this.messageCommands = formatCommandList("trollHell.messageCommands");
		this.fakeMessageFormat = plugin.getConfig().getString("trollHell.fakeMessageFormat");
	}

	private List<String> formatCommandList(String key) {
		List<String> ret = new ArrayList<>();
		for (String command : plugin.getConfig().getStringList(key)) {
			command = command.toLowerCase();
			ret.add(command.startsWith("/") ? command.substring(1) : command);
		}

		return ret;
	}
}
