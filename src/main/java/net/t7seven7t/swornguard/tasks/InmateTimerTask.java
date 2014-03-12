/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.tasks;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;

import org.bukkit.entity.Player;

/**
 * @author t7seven7t
 */
public class InmateTimerTask extends DatableRunnable {
	private final PlayerData data;
	private final SwornGuard plugin;
	private long lastMessage;
	
	public InmateTimerTask(final SwornGuard plugin, final Player player, final PlayerData data) {
		super(player);
		
		this.plugin = plugin;
		this.data = data;
	}
	
	@Override
	public void run() {
		if (data.isJailed() && player.isOnline()) {
			long now = System.currentTimeMillis();
			plugin.getJailHandler().checkPlayerInJail(player);
			if (now - data.getLastActivity() > 30000L) {
				if (now - lastMessage > 20000L) {
					player.sendMessage(FormatUtil.format(plugin.getMessage("jail_afk")));
					lastMessage = now;
				}
			} else {
				if (data.getJailTime() < 0) {
					data.setJailTime(0);
					plugin.getJailHandler().unjail(player, null);
					plugin.getLogHandler().log(plugin.getMessage("jail_log_finish_sentence"), player.getName());
				} else {
					data.setJailTime(data.getJailTime() - 1000);
				}
			}
		} else {
			this.cancel();
		}		
	}

}
