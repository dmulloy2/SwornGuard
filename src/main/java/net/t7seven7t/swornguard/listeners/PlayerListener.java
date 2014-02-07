/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.listeners;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.tasks.InmateTimerTask;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.Reloadable;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * @author t7seven7t
 */
public class PlayerListener implements Listener, Reloadable {
	private final SwornGuard plugin;
	private boolean combatLogDetectorEnabled;

	public PlayerListener(final SwornGuard plugin) {
		this.plugin = plugin;
		this.reload();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(final PlayerJoinEvent event) {
		// Store current time - will use it later
		final long now = System.currentTimeMillis();

		// Try to get the player's data from the cache otherwise create a new data entry
		PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
		if (data == null) {
			data = plugin.getPlayerDataCache().newData(event.getPlayer());
		}

		// Set first login time if the player's first visit to the server
		if (data.getFirstLogin() == 0)
			data.setFirstLogin(now);

		// Set most recent login time (now)
		data.setLastOnline(now);

		// Add to the number of times the player has joined the server
		data.setLogins(data.getLogins() + 1);

		// Add the player's ip address to the address list
		String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
		if (data.getIpAddressList().isEmpty() || !data.getIpAddressList().contains(ip))
			data.getIpAddressList().add(ip);

		// Hide vanished players from newly joined players.
		if (! plugin.getPermissionHandler().hasPermission(event.getPlayer(), PermissionType.VANISH_SPY.permission)) {
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				if (plugin.getPlayerDataCache().getData(player).isVanished()) {
					event.getPlayer().hidePlayer(player);
				}
			}
		}

		if (data.isUnjailNextLogin()) {
			plugin.getJailHandler().release(event.getPlayer());
		} else if (data.isJailed()) {
			new InmateTimerTask(plugin, event.getPlayer(), data).runTaskTimer(plugin, 20L, 20L);
		}

		plugin.getTrollHandler().handleJoin(event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(final PlayerQuitEvent event) {
		// Treat as player disconnect
		onPlayerDisconnect(event.getPlayer());
		plugin.getTrollHandler().handleQuit(event);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(final PlayerKickEvent event) {
		// Treat as player disconnect
		onPlayerDisconnect(event.getPlayer());
		plugin.getTrollHandler().handleKick(event);
	}

	public void onPlayerDisconnect(final Player player) {
		final PlayerData data = plugin.getPlayerDataCache().getData(player);

		// Update spent time before setting their disconnect time
		data.updateSpentTime();
		data.setLastOnline(System.currentTimeMillis());

		// Check if player is combat logging
		if (combatLogDetectorEnabled) {
			plugin.getCombatLogDetector().check(player);
		}

		// Take player out of patrol mode/inspecting/vanish before they log out.
		if (data.isCooldownPatrolling() || data.isPatrolling()) {
			plugin.getPatrolHandler().unAutoPatrolNoCooldown(player);
		}

		if (data.isInspecting()) {
			plugin.getPatrolHandler().returnFromInspecting(player);
		}

		if (data.isVanished()) {
			plugin.getPatrolHandler().vanish(player, false);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(final PlayerDeathEvent event) {
		PlayerData data = plugin.getPlayerDataCache().getData(event.getEntity());
		data.setDeaths(data.getDeaths() + 1);
		data.setLastAttacked(0);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerPickupItem(final PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data != null) {
				// If player is vanished they shouldn't pick up items lest confuse people
				// If player is jailed they shouldn't pick up items
				if (data.isVanished() || data.isJailed()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDropItem(final PlayerDropItemEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data != null) {
				if (data.isJailed()) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + plugin.getMessage("jail_cannot_drop_items"));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data != null) {
				// Re-set fly abilities for patrollers upon changing worlds
				if (data.isPatrolling() || data.isInspecting()) {
					event.getPlayer().setAllowFlight(true);
					event.getPlayer().setFlying(true);
				}
			}
		}	
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(final PlayerMoveEvent event) {
		if (plugin.getPlayerDataCache().getData(event.getPlayer()).isJailed())
			plugin.getPlayerDataCache().getData(event.getPlayer()).setLastActivity(System.currentTimeMillis());
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerTeleport(final PlayerTeleportEvent event) {
		Player player = event.getPlayer();
		if (player != null) {
			// Update last teleport time, prevents false pings
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			if (data != null) {
				data.setLastTeleport(System.currentTimeMillis());
			}
		}
	}

	@Override
	public void reload() {
		this.combatLogDetectorEnabled = plugin.getConfig().getBoolean("combatLogDetectorEnabled");
	}

}
