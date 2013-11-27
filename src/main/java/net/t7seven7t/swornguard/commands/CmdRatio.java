/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.commands;

import net.t7seven7t.swornguard.SwornGuard;
import net.t7seven7t.swornguard.permissions.PermissionType;
import net.t7seven7t.swornguard.types.PlayerData;

import org.bukkit.OfflinePlayer;

/**
 * @author t7seven7t
 */
public class CmdRatio extends SwornGuardCommand {

	public CmdRatio(SwornGuard plugin) {
		super(plugin);
		this.name = "ratio";
		this.description = plugin.getMessage("desc_ratio");
		this.permission = PermissionType.CMD_RATIO.permission; 
		this.optionalArgs.add("player");
		this.usesPrefix = true;
	}
	
	@Override
	public void perform() {
		OfflinePlayer target = getTarget(0);
		if (target == null)
			return;
		
		PlayerData data = getPlayerData(target);

		sendMessage(plugin.getMessage("ratio_header"), target.getName());
		if (data.getDiamondMined() == 0)
			sendMessage(plugin.getMessage("ratio_not_mined"), plugin.getMessage("ratio_diamond"));
		else
			sendMessage(plugin.getMessage("ratio"), plugin.getMessage("ratio_diamond"), plugin.getXrayDetector().getDiamondRatio(target));
		if (data.getIronMined() == 0)
			sendMessage(plugin.getMessage("ratio_not_mined"), plugin.getMessage("ratio_iron"));
		else
			sendMessage(plugin.getMessage("ratio"), plugin.getMessage("ratio_iron"), plugin.getXrayDetector().getIronRatio(target));
	}
	
}
