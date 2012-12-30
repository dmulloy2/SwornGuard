package com.minesworn.swornguard.patrol.commands;

import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.commands.SCommandRoot;

public class SPCommandRoot extends SCommandRoot<SwornGuard> {

	public static CmdPatrol CMD_PATROL = new CmdPatrol();
	public static CmdAutoPatrol CMD_AUTOPATROL = new CmdAutoPatrol();
	public static CmdVanish CMD_VANISH = new CmdVanish();
	public static CmdVanishList CMD_VANISHLIST = new CmdVanishList();
	public static CmdCheatTeleport CMD_CHEATTELEPORT = new CmdCheatTeleport();
	
	public SPCommandRoot() {
		addCommand(CMD_PATROL);
		addCommand(CMD_AUTOPATROL);
		addCommand(CMD_VANISH);
		addCommand(CMD_VANISHLIST);
		addCommand(CMD_CHEATTELEPORT);
	}
	
}
