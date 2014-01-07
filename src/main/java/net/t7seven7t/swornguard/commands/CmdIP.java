/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.util.FormatUtil;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdIP extends PaginatedCommand {
	private OfflinePlayer target = null;
	private List<String> ipList = null;

	public CmdIP(SwornGuard plugin) {
		super(plugin);
		this.name = "ip";
		this.description = plugin.getMessage("desc_ip");
		this.permission = PermissionType.CMD_IP.permission;
		this.optionalArgs.add("player");
		this.optionalArgs.add("page");
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
		
		if (data.getIpAddressList() != null) {
			ipList = new ArrayList<String>();
			for (int i = data.getIpAddressList().size() - 1; i >= 0; i--) {
				ipList.add(data.getIpAddressList().get(i));
			}

			super.perform();
		} else {
			err(plugin.getMessage("error_no_ip_data"), target.getName());
		}

		// Clear variables
		target = null;
		ipList = null;
	}

	@Override
	public int getListSize() {
		return ipList.size();
	}

	@Override
	public String getHeader(int index) {
		return FormatUtil.format(plugin.getMessage("ip_header"), target.getName(), index, getPageCount());
	}

	@Override
	public String getLine(int index) {
		return FormatUtil.format("&e{0}", ipList.get(index));
	}

}
