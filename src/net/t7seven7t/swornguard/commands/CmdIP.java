/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;
import net.t7seven7t.swornguard.util.FormatUtil;

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
	}
	
	@Override
	public void perform() {
		if (args.length == 0 && isPlayer())
			target = player;
		else if (args.length > 0)
			target = getTarget(args[0]);
		if (target == null)
			return;
		
		PlayerData data = plugin.getPlayerDataCache().getData(target);
		
		ipList = new ArrayList<String>();
		for (int x = data.getIpAddressList().size() - 1; x >= 0; x--) {
			ipList.add(data.getIpAddressList().get(x));
		}
		
		super.perform();
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
