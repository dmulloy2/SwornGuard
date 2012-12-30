package com.minesworn.swornguard.commands;

import com.minesworn.swornguard.Config;
import com.minesworn.swornguard.SwornGuard;
import com.minesworn.swornguard.core.commands.SCommandRoot;

public class SGCommandRoot extends SCommandRoot<SwornGuard> {
		
	public static CmdBanInfo CMD_BANINFO = new CmdBanInfo();
	public static CmdInfo CMD_INFO = new CmdInfo();
	public static CmdIP CMD_IP = new CmdIP();
	public static CmdLegit CMD_LEGIT = new CmdLegit();
	public static CmdModBot CMD_MODBOT = new CmdModBot();
	public static CmdNote CMD_NOTE = new CmdNote();
	public static CmdRatio CMD_RATIO = new CmdRatio();
	public static CmdShow CMD_SHOW = new CmdShow();
	public static CmdSInfo CMD_SINFO = new CmdSInfo();
	public static CmdReload CMD_RELOAD = new CmdReload();
	
	public SGCommandRoot() {
		super();
		addCommand(CMD_BANINFO);
		addCommand(CMD_INFO);
		addCommand(CMD_IP);
		addCommand(CMD_NOTE);
		addCommand(CMD_SHOW);
		addCommand(CMD_SINFO);
		addCommand(CMD_RELOAD);
		if (Config.enableXrayDetector) {
			addCommand(CMD_LEGIT);
			addCommand(CMD_RATIO);
		}
		if (Config.enableAutoModeratorBot)
			addCommand(CMD_MODBOT);
	}

}
