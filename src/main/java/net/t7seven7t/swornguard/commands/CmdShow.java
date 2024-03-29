/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import net.dmulloy2.util.FormatUtil;
import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.types.Permission;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdShow extends PaginatedCommand {
	private OfflinePlayer target = null;
	private List<String> profilerList = null;

	public CmdShow(SwornGuard plugin) {
		super(plugin);
		this.name = "show";
		this.aliases.add("s");
		this.description = plugin.getMessage("desc_show");
		this.permission = Permission.CMD_SHOW;
		this.addOptionalArg("player");
		this.addOptionalArg("page");
		this.pageArgIndex = 1;
		this.usesPrefix = true;
	}

	@Override
	public void perform() {
		target = getTarget(0);
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);
		if (data == null)
			return;
		
		if (data.getProfilerList() != null) {
			profilerList = new ArrayList<String>();
			for (int x = data.getProfilerList().size() - 1; x >= 0; x--) {
				profilerList.add(data.getProfilerList().get(x));
			}

			super.perform();
		} else {
			err(plugin.getMessage("error_no_profiler_data"), target.getName());
		}

		target = null;
		profilerList = null;
	}

	@Override
	public int getListSize() {
		return profilerList.size();
	}

	@Override
	public String getHeader(int index) {
		return FormatUtil.format(plugin.getMessage("profiler_header"), target.getName(), index, getPageCount());
	}

	@Override
	public String getLine(int index) {
		return FormatUtil.format("&e{0}", profilerList.get(index));
	}

}
