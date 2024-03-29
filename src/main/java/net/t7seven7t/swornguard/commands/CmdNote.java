/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import net.dmulloy2.util.FormatUtil;
import net.dmulloy2.util.TimeUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class CmdNote extends SwornGuardCommand {

	public CmdNote(SwornGuard plugin) {
		super(plugin);
		this.name = "note";
		this.aliases.add("n");
		this.description = plugin.getMessage("desc_note");
		this.permission = Permission.CMD_NOTE;
		this.addRequiredArg("player");
		this.addRequiredArg("note");	
		this.usesPrefix = true;
	}
	
	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;
		
		final PlayerData data = getPlayerData(target);
		
		final StringBuilder line = new StringBuilder();
		line.append(FormatUtil.format(plugin.getMessage("note_format"), TimeUtil.getLongDateCurr(), sender.getName()));
		for (int x = 1; x < args.length; x++)
			line.append(args[x] + " ");
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				synchronized(data) {
					data.getProfilerList().add(line.toString());
				}
			}
			
		}.runTaskAsynchronously(plugin);

		sendMessage(plugin.getMessage("note_confirm"));
	}
	
}
