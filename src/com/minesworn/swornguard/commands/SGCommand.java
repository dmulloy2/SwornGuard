package com.minesworn.swornguard.commands;

import org.bukkit.OfflinePlayer;

import com.minesworn.swornguard.PlayerInfo;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.commands.SCommand;
import com.minesworn.swornguard.core.util.Util;

public abstract class SGCommand extends SCommand<SwornGuard> {	
	
	OfflinePlayer getTarget(String name) {
		OfflinePlayer target = Util.matchOfflinePlayer(name);
		if (target == null) {
			errorMessage(SwornGuard.lang.getErrorMessage("playernotfound"));
		}
		return target;
	}
	
	PlayerInfo getPlayerInfo(OfflinePlayer target) {
		return SwornGuard.playerdatabase.getPlayer(target.getName());
	}
	
}
