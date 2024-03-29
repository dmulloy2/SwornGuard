/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dmulloy2.types.IPermission;

/**
 * @author t7seven7t
 */

@Getter
@AllArgsConstructor
public enum Permission implements IPermission {
	ALLOW_BLOCKED_COMMANDS("allow.blockedcommands"),
	ALLOW_FLY("canfly"),
	ALLOW_JAIL_BUILD("allow.jailbuild"),
	ALLOW_SPAM("allow.spam"),
	ALLOW_USE_COMMANDS_HELL("allow.usecommandshell"),
	ALLOW_USE_COMMANDS_JAILED("allow.usecommandsjailed"),
	CMD_ALT("cmd.alt"),
	CMD_AUTO_PATROL("cmd.autopatrol"),
	CMD_BAN("cmd.ban"),
	CMD_BAN_INFO("cmd.baninfo"),
	CMD_CHEAT_TELEPORT("cmd.cheatteleport"),
	CMD_INFO("cmd.info"),
	CMD_INFO_OTHERS("cmd.info.others"),
	CMD_IP("cmd.ip"),
	CMD_JAIL("cmd.jail"),
	CMD_JAIL_CHECK("cmd.jailcheck"),
	CMD_JAIL_HELP("cmd.jailhelp"),
	CMD_JAIL_REASON("cmd.jailreason"),
	CMD_JAIL_SET("cmd.jailset"),
	CMD_JAIL_STATUS("cmd.jailstatus"),
	CMD_JAIL_TIME("cmd.jailtime"),
	CMD_KICK("cmd.kick"),
	CMD_LEADERBOARD("cmd.leaderboard"),
	CMD_LEGIT("cmd.legit"),
	CMD_NOTE("cmd.note"),
	CMD_PATROL("cmd.patrol"),
	CMD_RATIO("cmd.ratio"),
	CMD_RELOAD("cmd.reload"),
	CMD_SERVER_INFO("cmd.serverinfo"),
	CMD_SHOW("cmd.show"),
	CMD_TROLL_BAN("troll.ban"),
	CMD_TROLL_HELL("troll.hell"),
	CMD_TROLL_MUTE("troll.mute"),
	CMD_UNBAN("cmd.unban"),
	CMD_UNJAIL("cmd.unjail"),
	CMD_VANISH("cmd.vanish"),
	CMD_VANISH_LIST("cmd.vanishlist"),
	CREEPFUN("creepfun"),
	FIREWORK("firework"),
	SHOW_CHEAT_REPORTS("showcheatreports"),
	TROLL_CHECK("troll.status"),
	TROLL_EXEMPT("troll.exempt"),
	TROLL_SPY("troll.spy"),
	VANISH_SPY("vanishspy")
	;

	private final String node;
}