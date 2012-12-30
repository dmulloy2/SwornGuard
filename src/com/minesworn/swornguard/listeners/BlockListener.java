package com.minesworn.swornguard.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.detectors.XrayDetector;

public class BlockListener implements Listener {

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getPlayer().getName());
		i.setBlockBuildCount(i.getBlockBuildCount() + 1);
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getPlayer().getName());
		i.setBlockDeleteCount(i.getBlockDeleteCount() + 1);
		if (Config.enableXrayDetector)
			XrayDetector.addBlock(e.getBlock().getType(), e.getPlayer());
	}
}
