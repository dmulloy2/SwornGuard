/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard;

import java.io.File;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.t7seven7t.swornguard.events.JailEvent;
import net.t7seven7t.swornguard.events.UnjailEvent;
import net.t7seven7t.swornguard.io.FileSerialization;
import net.t7seven7t.swornguard.tasks.InmateTimerTask;
import net.t7seven7t.swornguard.types.JailData;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.TimeUtil;

/**
 * @author t7seven7t
 */
public class JailHandler {
	private final SwornGuard plugin;
	private final JailData jail;
	
	public JailHandler(final SwornGuard plugin) {
		this.plugin = plugin;
		
		// Load jail settings
		final File file = new File(plugin.getDataFolder(), "jail.yml");
		if (file.exists()) {
			jail = FileSerialization.load(file, JailData.class);
		} else {
			jail = new JailData();
		}
	}
	
	public void saveJail() {
		FileSerialization.save(jail, new File(plugin.getDataFolder(), "jail.yml"));
	}
	
	public void unjail(final OfflinePlayer offlinePlayer, String unjailer) {
		PlayerData data = plugin.getPlayerDataCache().getData(offlinePlayer);
		
		// Player isn't jailed
		if (!data.isJailed())
			return;
		
		UnjailEvent event = new UnjailEvent(offlinePlayer);
		plugin.getServer().getPluginManager().callEvent(event);
				
		data.setJailed(false);
		data.setJailTime(0);
		if (unjailer != null)
			data.getProfilerList().add(FormatUtil.format(	plugin.getMessage("profiler_event"), 
															TimeUtil.getLongDateCurr(), 
															FormatUtil.format(plugin.getMessage("profiler_unjail"), unjailer)));
		
		if (offlinePlayer.isOnline()) {
			release(offlinePlayer.getPlayer());
		} else {
			data.setUnjailNextLogin(true);
		}
	}
	
	public void release(final Player player) {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setUnjailNextLogin(false);
		player.teleport(jail.getExit());
		player.sendMessage(FormatUtil.format(plugin.getMessage("jail_unjail")));
	}
	
	public void jail(final OfflinePlayer offlinePlayer, final long time, final String reason, final String jailer) {
		PlayerData data = plugin.getPlayerDataCache().getData(offlinePlayer);
		
		// Player is already jailed.
		if (data.isJailed())
			return;
		
		JailEvent event = new JailEvent(offlinePlayer);
		plugin.getServer().getPluginManager().callEvent(event);
		
		if (event.isCancelled())
			return;
		
		data.setJailed(true);
		data.setLastJail(System.currentTimeMillis());
		data.setLastJailer(jailer);
		data.setLastJailReason(reason);
		if (data.getJails() == 0)
			data.setJails(1);
		else
			data.setJails(data.getJails() + 1);
		data.setJailTime(time);
		data.getProfilerList().add(FormatUtil.format(	plugin.getMessage("profiler_event"), 
														TimeUtil.getLongDateCurr(), 
														FormatUtil.format(plugin.getMessage("profiler_jail"), time, reason, jailer)));
		
		if (offlinePlayer.isOnline()) {
			Player player = offlinePlayer.getPlayer();
			
			// Check if player is riding other entities
			if (player.isInsideVehicle())
				player.getVehicle().eject();
			
			// Teleport player to jail
			player.teleport(jail.getSpawn());
			player.sendMessage(FormatUtil.format(plugin.getMessage("jail_jail"), TimeUtil.formatTime(time), reason, jailer));
			
			// Begin count down of player's time
			new InmateTimerTask(plugin, player, data).runTaskTimer(plugin, 20L, 20L);
		}
	}
	
	public void checkPlayerInJail(final Player player) {
		if (!jail.isInside(player.getLocation())) {
			player.teleport(jail.getSpawn());
			player.sendMessage(FormatUtil.format(plugin.getMessage("jail_escape")));
		}
	}
	
	public JailData getJail() {
		return jail;
	}
	
}
