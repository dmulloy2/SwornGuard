/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands.troll;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.commands.SwornGuardCommand;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;
import net.t7seven7t.swornguard.util.TimeUtil;

import org.bukkit.OfflinePlayer;

/**
 * @author dmulloy2
 */
public class CmdTrollCheck extends SwornGuardCommand {

	public CmdTrollCheck(SwornGuard plugin) {
		super(plugin);
		this.name = "trollcheck";
		this.aliases.add("trollstatus");
		this.optionalArgs.add("player");
		this.description = "Check a player''s troll status";
		this.permission = PermissionType.TROLL_CHECK.permission;
		this.usesPrefix = false;
	}

	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0, true);
		if (target == null)
			return;

		PlayerData data = getPlayerData(target);
		if (data == null)
			return;

		if (! data.isTrollHell()) {
			sendMessage("&e{0} is not in troll hell.", target.getName());
			return;
		}

		List<String> lines = new ArrayList<String>();

		StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format("&ePlayer &a{0} &eis currently {1}&ein troll hell.", target.getName(), data.isTrollHell() ? "" : "not "));
		lines.add(line.toString());

		if (data.isTrollMuted()) {
			line = new StringBuilder();
			line.append(FormatUtil.format("&eTroll Muted: &ctrue"));
			lines.add(line.toString());
		}

		if (data.isTrollBanned()) {
			line = new StringBuilder();
			line.append(FormatUtil.format("&eTroll Banned: &ctrue"));
			lines.add(line.toString());
		}

		line = new StringBuilder();
		String lastTroller = data.getLastTroller();
		long lastTrollTime = data.getLastTrollHell();
		String lastTrollReason = data.getLastTrollReason();
		line.append(FormatUtil.format("&eLast put in troll hell by {0} on {1} ({2}) for {3}", 
				lastTroller == null ? "N/A" : lastTroller,
				lastTrollTime == 0 ? "N/A" : TimeUtil.getSimpleDate(lastTrollTime), 
				lastTrollTime == 0 ? "N/A" : TimeUtil.formatTimeDifference(lastTrollTime, System.currentTimeMillis()),
				lastTrollReason == null ? "unspecified" : lastTrollReason));
		lines.add(line.toString());

		for (String s : lines)
			sendMessage(s);
	}
}