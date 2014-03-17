/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.handlers;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.types.TrollType;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.TimeUtil;

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
		String send = "&eYou have put {0} in troll hell for {1}";
		String profiler = "&b{0} was put in troll hell by {1} for {2}";

		data.setTrollHell(true);

		if (type == TrollType.MUTE) {
			send = "&eYou have troll muted {0} for {1}";
			profiler = "&b{0} was troll muted by {1} for {2}";
			data.setTrollMuted(true);
		} else if (type == TrollType.BAN) {
			send = "&eYou have troll banned {0} for {1}";
			profiler = "&b{0} was troll banned by {1} for {2}";
			data.setTrollBanned(true);
		}

		if (troll.isOnline()) {
			forceIntoPublicChat(troll.getPlayer());

			// Hide players
			for (Player online : plugin.getServer().getOnlinePlayers()) {
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

		send = FormatUtil.format(send, troll.getName(), reason);
		sender.sendMessage(send);

		profiler = FormatUtil.format(profiler, troll.getName(), sender.getName(), reason);
		data.getProfilerList().add(FormatUtil.format(plugin.getMessage("profiler_event"), TimeUtil.getLongDateCurr(), profiler));
		plugin.getLogHandler().log(ChatColor.stripColor(profiler));

		data.setTrollHells(data.getTrollHells() + 1);
		data.setLastTroller(sender.getName());
		data.setLastTrollHell(System.currentTimeMillis());
		data.setLastTrollReason(reason);
	}

	public final void freeFromHell(CommandSender sender, OfflinePlayer troll, TrollType type) {
		PlayerData data = plugin.getPlayerDataCache().getData(troll);
		String send = "&eYou have freed {0} from troll hell";
		String profiler = "&b{0} was freed from troll hell by {1}";

		data.setTrollBanned(false);
		data.setTrollMuted(false);

		if (type == TrollType.BAN) {
			send = "&eYou have troll unbanned {0}";
			profiler = "&b{0} was troll unbanned by {1}";
		} else if (type == TrollType.MUTE) {
			send = "&eYou have troll unmuted {0}";
			profiler = "&b{0} was troll unmuted by {1}";
		} else if (type == TrollType.HELL) {
			data.setTrollHell(false);

			// Show players
			if (troll.isOnline()) {
				for (Player online : plugin.getServer().getOnlinePlayers()) {
					PlayerData data1 = plugin.getPlayerDataCache().getData(online);
					if (! data1.isVanished()) {
						troll.getPlayer().showPlayer(online);
					}
				}
			}
		}

		send = FormatUtil.format(send, troll.getName());
		sender.sendMessage(send);

		profiler = FormatUtil.format(plugin.getMessage("profiler_event"), TimeUtil.getLongDateCurr(),
				FormatUtil.format(profiler, troll.getName(), sender.getName()));
		data.getProfilerList().add(profiler);
		plugin.getLogHandler().log(ChatColor.stripColor(profiler));
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
			String node = plugin.getPermissionHandler().getPermissionString(PermissionType.TROLL_SPY.permission);

			for (Player p : plugin.getServer().getOnlinePlayers()) {
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
		// Weird exception for newly joined players
		if (trollData != null && trollData.isTrollHell()) {
			for (Player online : plugin.getServer().getOnlinePlayers()) {
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

				if (plugin.getPermissionHandler().hasPermission(online, PermissionType.TROLL_SPY.permission)) {
					String lastReason = trollData.getLastTrollReason();
					online.sendMessage(FormatUtil.format(plugin.getMessage("troll_join"), event.getPlayer().getName(),
							lastReason != null ? lastReason : "not applicable"));
				}
			}

			forceIntoPublicChat(troll);
			event.setJoinMessage(null);
		} else {
			for (Player online : plugin.getServer().getOnlinePlayers()) {
				PlayerData data = plugin.getPlayerDataCache().getData(online);
				if (data.isTrollHell()) {
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

		for (Player online : plugin.getServer().getOnlinePlayers()) {
			PlayerData data = plugin.getPlayerDataCache().getData(online);
			if (data.isTrollHell()) {
				if (! trollData.isTrollMuted() && ! trollData.isTrollBanned()) {
					online.sendMessage(event.getQuitMessage());
				}
			}

			if (plugin.getPermissionHandler().hasPermission(online, PermissionType.TROLL_SPY.permission)) {
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