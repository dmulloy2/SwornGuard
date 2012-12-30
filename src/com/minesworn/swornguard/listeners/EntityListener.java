package com.minesworn.swornguard.listeners;

import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.detectors.AutoAttack;
import com.minesworn.swornguard.detectors.AutoClicker;
import com.minesworn.swornguard.detectors.CombatLogDetector;
import com.minesworn.swornguard.detectors.FactionBetrayalDetector;

public class EntityListener implements Listener {

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent e) {
		if (e.isCancelled())
			return;
				
		if (e.getDamager() instanceof Player) {
			Player damager = (Player) e.getDamager();
			PlayerInfo i = SwornGuard.playerdatabase.getPlayer(damager.getName());
			
			if (i.isAutoPatrolling() || i.isCheaterInspecting()) {
				e.setCancelled(true);
				return;
			}
			
			AutoAttack.check(damager, e.getEntity());
			if (Config.enableAutoClickerProtection)
				if (AutoClicker.check(damager)) {
					e.setCancelled(true);
					return;
				}
			
			if (e.getEntity() instanceof Player) {
				Player player = (Player) e.getEntity();
				if (Config.enableFactionBetrayalDetector && (SwornGuard.isPluginEnabled("Factions") || SwornGuard.isPluginEnabled("SwornNations")))
					FactionBetrayalDetector.checkFactionBetrayal(player, e.getDamage(), damager);
				
				if (player.getHealth() - e.getDamage() < 0) {
					if (System.currentTimeMillis() - i.getLastPlayerKill() > 300L) {
						i.setLastPlayerKill(System.currentTimeMillis());
						i.setPlayerKills(i.getPlayerKills() + 1);
					}
				}
				
			} else if (e.getEntity() instanceof Monster) {
				Monster mob = (Monster) e.getEntity();
				if (mob.getHealth() - e.getDamage() < 0) {
					if (System.currentTimeMillis() - i.getLastMobKill() > 300L) {
						i.setLastMobKill(System.currentTimeMillis());
						i.setMobKills(i.getMobKills() + 1);
					}
				}
			} else if (e.getEntity() instanceof Animals) {
				Animals mob = (Animals) e.getEntity();
				if (mob.getHealth() - e.getDamage() < 0) {
					if (System.currentTimeMillis() - i.getLastAnimalKill() > 300L) {
						i.setLastAnimalKill(System.currentTimeMillis());
						i.setAnimalKills(i.getAnimalKills() + 1);
					}
				}
			}
		}
		
		if (e.getEntity() instanceof Player) {
			if (Config.enableCombatLogDetector)
				CombatLogDetector.addAttacked((Player) e.getEntity(), e.getDamager());
		}
	}
	
	@EventHandler
	public void onPlayerDamageEvent(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			PlayerInfo i = SwornGuard.playerdatabase.getPlayer(Player.class.cast(e.getEntity()).getName());
			if (i.isAutoPatrolling() || i.isCheaterInspecting())
				e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getEntity().getName());
		i.setLastAttacked(0);
		
		i.setDeaths(i.getDeaths() + 1);
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent e) {
		if (SwornGuard.playerdatabase.getPlayer(e.getPlayer().getName()).isVanished())
			e.setCancelled(true);
	}
	
	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent e) {
		PlayerInfo i = SwornGuard.playerdatabase.getPlayer(e.getPlayer().getName());
		if (i.isAutoPatrolling() || i.isCheaterInspecting()) {
			e.getPlayer().setAllowFlight(true);
			e.getPlayer().setFlying(true);
		}
	}
	
}
