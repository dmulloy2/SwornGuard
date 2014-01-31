/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.types;

import net.t7seven7t.swornguard.SwornGuard;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;

/**
 * @author dmulloy2
 */

public class Preconditions {
	private final SwornGuard plugin;

	public Preconditions(SwornGuard plugin) {
		this.plugin = plugin;
	}

	public boolean isPlayerFallingIntoVoid(Player player) {
		Location loc = player.getLocation();
		if (loc.getBlockY() < 0) {
			return true;
		}
		
		for (int y = loc.getBlockY(); y >= 0; y--) {
			if (loc.getWorld().getBlockAt(loc.getBlockX(), y, loc.getBlockZ()).getType() != Material.AIR) {
				return false;
			}
		}

		return true;
	}

	public boolean isPlayerInsideCar(Player player) {
		if (player.isInsideVehicle()) {
			Entity ent = player.getVehicle();
			if (ent instanceof Vehicle) {
				Vehicle veh = (Vehicle) ent;
				if (veh instanceof Minecart) {
					Minecart cart = (Minecart) veh;
					Location loc = cart.getLocation();
					
					Material type = loc.getBlock().getType();
					
					return type != Material.POWERED_RAIL && type != Material.RAILS
							&& type != Material.DETECTOR_RAIL && type != Material.ACTIVATOR_RAIL;
				}
			}
		}
		
		return false;
	}
	
	public boolean isNewPlayerJoin(Player player) {
		if (! player.hasPlayedBefore()) {
			PlayerData data = plugin.getPlayerDataCache().getData(player);
			if (data != null) {
				return data.getOnlineTime() < 200L;
			}
			
			return true;
		}
		
		return false;
	}

	public boolean hasRecentlyTeleported(Player player) {
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data != null) {
			return System.currentTimeMillis() - data.getLastTeleport() < 60L;
		}

		return false;
	}
	
}
