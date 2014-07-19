/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.handlers;

import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.TimeUtil;
import net.dmulloy2.util.Util;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.TrollType;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.types.ChatMode;

/**
 * @author dmulloy2
 */

public class TrollHandler implements Listener {
	private final SwornGuard plugin;

	public TrollHandler(SwornGuard plugin) {
		this.plugin = plugin;
		this.registerEvents();
	}

	public final void putInHell(CommandSender sender, OfflinePlayer troll, TrollType type, String reason) {
		PlayerData data = plugin.getPlayerDataCache().getData(troll);
		// String send = "&eYou have put &c{0} &ein troll hell for &c{1}&e.";
		String profiler = "&b{0} was put in troll hell by {1} for {2}.";
		String broadcast = "&c{0} &eput &c{1} &ein troll hell for &c{2}&e.";

		data.setTrollHell(true);

		if (type == TrollType.MUTE) {
			// send = "&eYou have troll muted &c{0} &efor &c{1}&e.";
			profiler = "&b{0} was troll muted by {1} for {2}.";
			broadcast = "&c{0} &etroll muted &c{1} &efor &c{2}&e.";
			data.setTrollMuted(true);
		} else if (type == TrollType.BAN) {
			// send = "&eYou have troll banned &c{0} &efor &c{1}&e.";
			profiler = "&b{0} was troll banned by {1} for {2}.";
			broadcast = "&c{0} &etroll banned &c{1} &efor &c{2}&e.";
			data.setTrollBanned(true);
		}

		if (troll.isOnline()) {
			forceIntoPublicChat(troll.getPlayer());

			// Hide players
			for (Player online : Util.getOnlinePlayers()) {
				PlayerData data1 = plugin.getPlayerDataCache().getData(online);
				if (data1.isTrollHell()) {
					if (data.isTrollMuted() || data.isTrollBanned()) {
						troll.getPlayer().hidePlayer(online);
					}
				} else {
					troll.getPlayer().hidePlayer(online);
				}
			}
		}

		// send = FormatUtil.format(send, troll.getName(), reason);
		// sender.sendMessage(send);

		profiler = FormatUtil.format(profiler, troll.getName(), sender.getName(), reason);
		data.getProfilerList().add(FormatUtil.format(plugin.getMessage("profiler_event"), TimeUtil.getLongDateCurr(), profiler));
		plugin.getLogHandler().log(ChatColor.stripColor(profiler));

		broadcast = FormatUtil.format(broadcast, sender.getName(), troll.getName(), reason);
		plugin.getServer().broadcast(broadcast, plugin.getPermissionHandler().getPermissionString(Permission.TROLL_SPY));

		data.setTrollHells(data.getTrollHells() + 1);
		data.setLastTroller(sender.getName());
		data.setLastTrollHell(System.currentTimeMillis());
		data.setLastTrollReason(reason);
	}

	public final void freeFromHell(CommandSender sender, OfflinePlayer troll, TrollType type) {
		PlayerData data = plugin.getPlayerDataCache().getData(troll);
		// String send = "&eYou have freed &c{0} &efrom troll hell.";
		String profiler = "&b{0} was freed from troll hell by {1}.";
		String broadcast = "&c{0} &efreed &c{1} &efrom troll hell.";

		data.setTrollBanned(false);
		data.setTrollMuted(false);

		if (type == TrollType.BAN) {
			// send = "&eYou have troll unbanned &c{0}&e.";
			profiler = "&b{0} was troll unbanned by {1}.";
			broadcast = "&c{0} &etroll unbanned &c{1}&e.";
		} else if (type == TrollType.MUTE) {
			// send = "&eYou have troll unmuted &c{0}&e.";
			profiler = "&b{0} was troll unmuted by {1}.";
			broadcast = "&c{0} &etroll unmuted &c{1}&e.";
		} else if (type == TrollType.HELL) {
			data.setTrollHell(false);

			// Show players
			if (troll.isOnline()) {
				for (Player online : Util.getOnlinePlayers()) {
					PlayerData data1 = plugin.getPlayerDataCache().getData(online);
					if (! data1.isVanished()) {
						troll.getPlayer().showPlayer(online);
					}
				}
			}
		}

		// send = FormatUtil.format(send, troll.getName());
		// sender.sendMessage(send);

		profiler = FormatUtil.format(profiler, troll.getName(), sender.getName());
		data.getProfilerList().add(FormatUtil.format(plugin.getMessage("profiler_event"), TimeUtil.getLongDateCurr(), profiler));
		plugin.getLogHandler().log(ChatColor.stripColor(profiler));

		broadcast = FormatUtil.format(broadcast, sender.getName(), troll.getName());
		plugin.getServer().broadcast(broadcast, plugin.getPermissionHandler().getPermissionString(Permission.TROLL_SPY));
	}

	public final void forceIntoPublicChat(Player troll) {
		PlayerData data = plugin.getPlayerDataCache().getData(troll);
		if (data.isTrollHell()) {
			try {
				PluginManager pm = plugin.getServer().getPluginManager();
				if (pm.getPlugin("Factions") != null || pm.getPlugin("SwornNations") != null) {
					if (Conf.factionOnlyChat) {
						FPlayer fplayer = FPlayers.i.get(troll);
						if (fplayer.getChatMode() != ChatMode.PUBLIC) {
							fplayer.setChatMode(ChatMode.PUBLIC);
						}
					}
				}
			} catch (Throwable ex) {
				// Probably a different version of Factions
			}
		}
	}

	// ---- Event Listeners ---- //

	private final void registerEvents() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isTrollHell()) {
			event.getRecipients().clear();
			if (data.isTrollMuted() || data.isTrollBanned()) {
				event.getRecipients().add(event.getPlayer());
				return;
			}

			String admMsg = FormatUtil.format(plugin.getMessage("troll_format"), event.getPlayer().getName(), event.getMessage());
			String node = plugin.getPermissionHandler().getPermissionString(Permission.TROLL_SPY);

			for (Player p : Util.getOnlinePlayers()) {
				PlayerData data1 = plugin.getPlayerDataCache().getData(p);
				if (data1.isTrollHell()) {
					event.getRecipients().add(p);
				}

				if (p.hasPermission(node)) {
					p.sendMessage(admMsg);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player troll = event.getPlayer();
		PlayerData trollData = plugin.getPlayerDataCache().getData(troll);
		if (trollData != null && trollData.isTrollHell()) {
			for (Player online : Util.getOnlinePlayers()) {
				PlayerData data = plugin.getPlayerDataCache().getData(online);
				if (data.isTrollHell()) {
					if (! trollData.isTrollMuted() && ! trollData.isTrollBanned()) {
						online.sendMessage(event.getJoinMessage());
					} else {
						troll.hidePlayer(online);
					}
				} else {
					troll.hidePlayer(online);
				}

				if (plugin.getPermissionHandler().hasPermission(online, Permission.TROLL_SPY)) {
					String lastReason = trollData.getLastTrollReason();
					online.sendMessage(FormatUtil.format(plugin.getMessage("troll_join"), event.getPlayer().getName(),
							lastReason != null ? lastReason : "not applicable"));
				}
			}

			forceIntoPublicChat(troll);
			event.setJoinMessage(null);
		} else {
			for (Player online : Util.getOnlinePlayers()) {
				PlayerData data = plugin.getPlayerDataCache().getData(online);
				if (data != null && data.isTrollHell()) {
					troll.hidePlayer(online);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player troll = event.getPlayer();
		PlayerData trollData = plugin.getPlayerDataCache().getData(troll);
		if (! trollData.isTrollHell()) {
			return;
		}

		for (Player online : Util.getOnlinePlayers()) {
			PlayerData data = plugin.getPlayerDataCache().getData(online);
			if (data.isTrollHell()) {
				if (! trollData.isTrollMuted() && ! trollData.isTrollBanned()) {
					online.sendMessage(event.getQuitMessage());
				}
			}

			if (plugin.getPermissionHandler().hasPermission(online, Permission.TROLL_SPY)) {
				online.sendMessage(FormatUtil.format(plugin.getMessage("troll_leave"), event.getPlayer().getName()));
			}
		}

		event.setQuitMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerKick(PlayerKickEvent event) {
		Player troll = event.getPlayer();
		PlayerData trollData = plugin.getPlayerDataCache().getData(troll);
		if (! trollData.isTrollHell()) {
			return;
		}

		event.setLeaveMessage(null);
	}
}