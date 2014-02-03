/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.ChatMode;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;

/**
 * @author dmulloy2
 */

public class CmdTrollMute extends SwornGuardCommand {
	public CmdTrollMute(SwornGuard plugin) {
		super(plugin);
		this.name = "trollmute";
		this.aliases.add("hellmute");
		this.requiredArgs.add("player");
		this.description = "Silence a troll";
		this.permission = PermissionType.CMD_TROLL.permission;
		this.usesPrefix = false;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);
		if (data == null)
			return;

		if (target.isOnline() && plugin.getPermissionHandler().hasPermission(target.getPlayer(), PermissionType.TROLL_EXEMPT.permission)) {
			err("You may not troll mute &c{0}&4!", target.getName());
			data.setTrollMuted(false);
			return;
		}

		data.setTrollMuted(!data.isTrollMuted());

		if (target.isOnline()) {
			Player troll = target.getPlayer();

			for (Player p : plugin.getServer().getOnlinePlayers()) {
				if (! plugin.getPermissionHandler().hasPermission(p, PermissionType.TROLL_SPY.permission)) {
					if (data.isTrollHell()) {
						p.hidePlayer(troll);
						troll.hidePlayer(p);
					} else {
						p.showPlayer(troll);
						troll.showPlayer(p);
					}
				}
			}

			// dmulloy2 - try to force the troll into public chat,
			// since we can't really control faction chat
			if (data.isTrollMuted()) {
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

		String result = FormatUtil.format(plugin.getMessage("troll_hell"), target.getName(), data.isTrollHell() ? "in" : "freed from");
		sendMessage(result);
		plugin.getLogHandler().log(result);
	}

}