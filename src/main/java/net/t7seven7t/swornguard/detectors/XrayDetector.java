/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.detectors;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.events.CheatEvent;
import net.t7seven7t.swornguard.types.CheatType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.TimeUtil;
import net.t7seven7t.util.Util;

/**
 * @author t7seven7t
 */
public class XrayDetector implements Listener {
	private final SwornGuard plugin;
	private final double warnOnDiamondRatio;
	private final double warnOnIronRatio;
	
	public XrayDetector(final SwornGuard plugin) {
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
		this.warnOnDiamondRatio = plugin.getConfig().getDouble("xrayWarnOnDiamondRatio");
		this.warnOnIronRatio = plugin.getConfig().getDouble("xrayWarnOnIronRatio");
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(final BlockBreakEvent event) {
		if (!event.isCancelled() 
				&& (event.getBlock().getType().equals(Material.IRON_ORE) 
				|| event.getBlock().getType().equals(Material.DIAMOND_ORE) 
				|| event.getBlock().getType().equals(Material.STONE))) {
			
			final PlayerData data = plugin.getPlayerDataCache().getData(event.getPlayer());
			
			if (event.getBlock().getType().equals(Material.IRON_ORE))
				data.setIronMined(data.getIronMined() + 1);
			
			else if (event.getBlock().getType().equals(Material.DIAMOND_ORE))
				data.setDiamondMined(data.getDiamondMined() + 1);
			
			else if (event.getBlock().getType().equals(Material.STONE))
				data.setStoneMined(data.getStoneMined() + 1);
			
			checkRatio(event.getPlayer());
		}
	}
	
	public void checkRatio(final OfflinePlayer player) {
		checkRatio(player.getName());
	}
	
	public void checkRatio(final String name) {
		final PlayerData data = plugin.getPlayerDataCache().getData(name);
		if (data.getStoneMined() > 150 && TimeUtil.getTimeDifference(data.getLastXrayWarn(), System.currentTimeMillis()) > 45000L 
				&& ((warnOnIronRatio > 0 && data.getIronMined() > 0 && getIronRatio(name) > warnOnIronRatio)
				|| (warnOnDiamondRatio > 0 && data.getDiamondMined() > 0 && getDiamondRatio(name) > warnOnDiamondRatio))) {
			CheatEvent event = new CheatEvent(name, CheatType.XRAY, FormatUtil.format(plugin.getMessage("cheat_message"), name, "xraying"));
			plugin.getCheatHandler().announceCheat(event);
			data.setLastXrayWarn(System.currentTimeMillis());
		}
	}
		
	public double getDiamondRatio(final OfflinePlayer player) {
		return getDiamondRatio(player.getName());
	}
	
	public double getDiamondRatio(final String name) {
		PlayerData data = plugin.getPlayerDataCache().getData(name);
		return getRatio(data.getDiamondMined(), data.getDiamondMined() + data.getStoneMined());
	}
	
	public double getIronRatio(final OfflinePlayer player) {
		return getIronRatio(player.getName());
	}
	
	public double getIronRatio(final String name) {
		PlayerData data = plugin.getPlayerDataCache().getData(name);
		return getRatio(data.getIronMined(), data.getIronMined() + data.getStoneMined());
	}
	
	public double getRatio(final int top, final int bottom) {
		return Util.roundNumDecimals((double) top / bottom * 100, 2);
	}
	
	public void legit(final OfflinePlayer player) {
		final PlayerData data = plugin.getPlayerDataCache().getData(player);
		data.setIronMined(0);
		data.setDiamondMined(0);
		data.setStoneMined(0);
	}
	
}
