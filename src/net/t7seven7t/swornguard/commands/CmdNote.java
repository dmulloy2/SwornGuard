/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import org.bukkit.OfflinePlayer;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;
import net.t7seven7t.util.TimeUtil;

/**
 * @author t7seven7t
 */
public class CmdNote extends SwornGuardCommand {

	public CmdNote(SwornGuard plugin) {
		super(plugin);
		this.name = "note";
		this.aliases.add("n");
		this.description = plugin.getMessage("desc_note");
		this.permission = PermissionType.CMD_NOTE.permission;
		this.requiredArgs.add("player");
		this.requiredArgs.add("note");	
		this.usesPrefix = true;
	}
	
	public void perform() {
		OfflinePlayer target = getTarget(args[0]);
		if (target == null)
			return;
		
		final PlayerData data = getPlayerData(target);
		if (data == null)
			return;
		
		final StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("note_format"), TimeUtil.getLongDateCurr(), sender.getName()));
		for (int x = 1; x < args.length; x++)
			line.append(args[x] + " ");
		
		plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
			public void run() {
				synchronized(data) {
					data.getProfilerList().add(line.toString());
				}
			}
		});
		
		sendMessage(plugin.getMessage("note_confirm"));
	}
	
}
