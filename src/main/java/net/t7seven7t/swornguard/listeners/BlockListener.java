/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.listeners;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * @author t7seven7t
 */
public class BlockListener implements Listener {
	private final SwornGuard plugin;
	
	public BlockListener(final SwornGuard plugin) {
		this.plugin = plugin;
	}	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPlaceMonitor(final BlockPlaceEvent event) {
		if (!event.isCancelled()) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			data.setBlocksBuilt(data.getBlocksBuilt() + 1);
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreakMonitor(final BlockBreakEvent event) {
		if (!event.isCancelled()) {
			PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			data.setBlocksDeleted(data.getBlocksDeleted() + 1);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockPlace(BlockPlaceEvent event) {
		if (!event.isCancelled())
			if (!canPlayerBuildHere(event.getBlock(), event.getPlayer()))
				event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.isCancelled())
			if (!canPlayerBuildHere(event.getBlock(), event.getPlayer()))
				event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onBlockDamage(BlockDamageEvent event) {
		if (!event.isCancelled())
			if (!canPlayerBuildHere(event.getBlock(), event.getPlayer()))
				event.setCancelled(true);
	}
	
	public boolean canPlayerBuildHere(Block block, Player player) {
		if (!plugin.getJailHandler().getJail().isInside(block.getLocation()))
			return true;
		
		if (!plugin.getPermissionHandler().hasPermission(player, Permission.ALLOW_JAIL_BUILD))
			return false;
		return true;
	}
	
}
 