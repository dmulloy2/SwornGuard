package com.minesworn.swornguard.detectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.minesworn.swornguard.Cheat;
import com.minesworn.swornguard.PermissionsManager;
import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.events.CheatEvent;

public class FlyDetector {

	public FlyDetector() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SwornGuard.p, new Runnable() {

			@Override
			public void run() {
				step();
			}
			
		}, 20L, 10L);
	}
	
	private void step() {
//		long now = System.nanoTime();
		for (final Player p : Bukkit.getOnlinePlayers()) {
			if (!PermissionsManager.hasPermission(p, Permission.CAN_FLY.node) 
					&& !p.getAllowFlight() && (isVeloStrange(p) || (getDistToGround(p) > 9 && !isNearWater(p)))) {
				final PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
				final Vector prevL = p.getLocation().toVector();
				if (System.currentTimeMillis() - i.getLastWarnedForFlying() > 45000L) {
					i.setLastWarnedForFlying(System.currentTimeMillis());
					Bukkit.getScheduler().scheduleSyncDelayedTask(SwornGuard.p, new Runnable() {
						@Override
						public void run() {
							checkFlying(p, prevL);
						}
					}, 20L);
				}
			}
		}
//		System.out.println("step: " + (System.nanoTime() - now));
	}
	
	private void checkFlying(Player p, Vector prevL) {
		Vector l = p.getLocation().toVector();
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(p.getName());
		
		// if (System.currentTimeMillis() - i.getFlyTicks() > 30000L); - WTF Does this line even do? I don't remember.. lol
		
		if (l.getY() > prevL.getY() || (l.getY() >= prevL.getY() && (Math.abs(l.getX() - prevL.getX()) > 6 || Math.abs(l.getZ() - prevL.getZ()) > 6))) {
			if (i.getFlyTicks() != 0) {
				CheatEvent e = new CheatEvent(p, "[CHEATER] I think that " + p.getName() + " is flying!", Cheat.FLYING);
				SwornGuard.announceCheat(e);
				i.setLastWarnedForFlying(System.currentTimeMillis());
				i.setFlyTicks(0);
				return;
			}
			
			i.setFlyTicks(System.currentTimeMillis());
		}
		
		i.setLastWarnedForFlying(0);
	}
	
	private boolean isVeloStrange(Player p) {
		if (p.getVelocity().getY() < -2)
			return true;	
		return false;
	}
	
	private boolean isNearWater(Player p) {
		Location[] locs = new Location[5];
		locs[0] = p.getLocation();
		locs[1] = p.getLocation().add(1, 0, 0);
		locs[2] = p.getLocation().add(-1, 0, 0);
		locs[3] = p.getLocation().add(0, 0, 1);
		locs[4] = p.getLocation().add(0, 0, -1);
		for (Location l : locs) {
			Block b = l.getWorld().getBlockAt(l);
			if (b != null && (b.isLiquid() || b.getType() == Material.LADDER 
					|| b.getType() == Material.VINE)) {
				return true;
			}
		}
		return false;
	}
	
	private int getDistToGround(Player p) {
		Location loc = p.getLocation();
		Material m;
		int count = 0;
		while(loc.getBlockY() > 0 && ((m = loc.getWorld().getBlockAt(loc).getType()) == Material.AIR 
				|| m == Material.WATER) && count < 10) {
			loc = loc.subtract(0, 1, 0);
			count++;
		}
		return count;
	}
	
}
