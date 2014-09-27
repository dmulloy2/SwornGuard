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

	public final void putInHell(CommandSender sender, PlayerData data, OfflinePlayer troll, TrollType type, String reason) {
		String profiler = "&b{0} was put in troll hell by {1} for {2}.";
		String broadcast = "&c{0} &eput &c{1} &ein troll hell for &c{2}&e.";

		data.setTrollHell(true);

		if (type == TrollType.MUTE) {
			profiler = "&b{0} was troll muted by {1} for {2}.";
			broadcast = "&c{0} &etroll muted &c{1} &efor &c{2}&e.";
			data.setTrollMuted(true);
		} else if (type == TrollType.BAN) {
			profiler = "&b{0} was troll banned by {1} for {2}.";
			broadcast = "&c{0} &etroll banned &c{1} &efor &c{2}&e.";
			data.setTrollBanned(true);
		}

		if (troll.isOnline()) {
			Player player = troll.getPlayer();
			forceIntoPublicChat(data, player);

			// Hide players
			for (Player online : Util.getOnlinePlayers()) {
				PlayerData data1 = plugin.getPlayerDataCache().getData(online);
				if (data1.isTrollHell()) {
					if (data.isTrollMuted() || data.isTrollBanned()) {
						player.hidePlayer(online);
					}
				} else {
					player.hidePlayer(online);
				}
			}
		}

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

	public final void freeFromHell(CommandSender sender, PlayerData data, OfflinePlayer troll, TrollType type) {
		String profiler = "&b{0} was freed from troll hell by {1}.";
		String broadcast = "&c{0} &efreed &c{1} &efrom troll hell.";

		data.setTrollBanned(false);
		data.setTrollMuted(false);

		if (type == TrollType.BAN) {
			profiler = "&b{0} was troll unbanned by {1}.";
			broadcast = "&c{0} &etroll unbanned &c{1}&e.";
		} else if (type == TrollType.MUTE) {
			profiler = "&b{0} was troll unmuted by {1}.";
			broadcast = "&c{0} &etroll unmuted &c{1}&e.";
		} else if (type == TrollType.HELL) {
			data.setTrollHell(false);

			// Show players
			if (troll.isOnline()) {
				Player player = troll.getPlayer();
				for (Player online : Util.getOnlinePlayers()) {
					PlayerData data1 = plugin.getPlayerDataCache().getData(online);
					if (! data1.isVanished()) {
						player.showPlayer(online);
					}
				}
			}
		}

		profiler = FormatUtil.format(profiler, troll.getName(), sender.getName());
		data.getProfilerList().add(FormatUtil.format(plugin.getMessage("profiler_event"), TimeUtil.getLongDateCurr(), profiler));
		plugin.getLogHandler().log(ChatColor.stripColor(profiler));

		broadcast = FormatUtil.format(broadcast, sender.getName(), troll.getName());
		plugin.getServer().broadcast(broadcast, plugin.getPermissionHandler().getPermissionString(Permission.TROLL_SPY));
	}

	public final void forceIntoPublicChat(PlayerData data, Player troll) {
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
			}
		}
	}

	// ---- Event Listeners

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
				event.getRecipients().add(player);
				return;
			}

			String admMsg = FormatUtil.format(plugin.getMessage("troll_format"), player.getName(), event.getMessage());
			String node = plugin.getPermissionHandler().getPermissionString(Permission.TROLL_SPY);

			for (Player online : Util.getOnlinePlayers()) {
				PlayerData data1 = plugin.getPlayerDataCache().getData(online);
				if (data1.isTrollHell()) {
					event.getRecipients().add(online);
				}

				if (online.hasPermission(node)) {
					online.sendMessage(admMsg);
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

			forceIntoPublicChat(trollData, troll);
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
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (! data.isTrollHell()) {
			return;
		}

		for (Player online : Util.getOnlinePlayers()) {
			PlayerData data1 = plugin.getPlayerDataCache().getData(online);
			if (data1.isTrollHell()) {
				if (! data1.isTrollMuted() && ! data1.isTrollBanned()) {
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
		Player player = event.getPlayer();
		PlayerData data = plugin.getPlayerDataCache().getData(player);
		if (data.isTrollHell()) {
			event.setLeaveMessage(null);
		}
	}
}