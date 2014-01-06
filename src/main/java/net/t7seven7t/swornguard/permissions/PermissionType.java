/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.permissions;

/**
 * @author t7seven7t
 */
public enum PermissionType {
	ALLOW_BLOCKED_COMMANDS("allow.blockedcommands"),
	ALLOW_USE_COMMANDS_JAILED("allow.usecommandsjailed"),
	ALLOW_USE_COMMANDS_HELL("allow.usecommandshell"),
	ALLOW_FLY("canfly"),
	ALLOW_SPAM("allow.spam"),
	ALLOW_JAIL_BUILD("allow.jailbuild"),
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
	CMD_JAIL_MUTE("cmd.jailmute"),
	CMD_JAIL_REASON("cmd.jailreason"),
	CMD_JAIL_SET("cmd.jailset"),
	CMD_JAIL_STATUS("cmd.jailstatus"),
	CMD_JAIL_TIME("cmd.jailtime"),
	CMD_KICK("cmd.kick"),
	CMD_LEGIT("cmd.legit"),
	CMD_NOTE("cmd.note"),
	CMD_PATROL("cmd.patrol"),
	CMD_RATIO("cmd.ratio"),
	CMD_RELOAD("cmd.reload"),
	CMD_SHOW("cmd.show"),
	CMD_SERVER_INFO("cmd.serverinfo"),
	CMD_TROLL("cmd.troll"),
	CMD_UNJAIL("cmd.unjail"),
	CMD_UNBAN("cmd.unban"),
	CMD_VANISH("cmd.vanish"),
	CMD_VANISH_LIST("cmd.vanishlist"),
	SHOW_CHEAT_REPORTS("showcheatreports"),
	TROLL_SPY("trollspy"),
	TROLL_EXEMPT("troll.exempt"),
	VANISH_SPY("vanishspy"),
	FIREWORK("firework"),
	CREEPFUN("creepfun");
	
	public final Permission permission;
	PermissionType(final String node) {
		permission = new Permission(node);
	}
}
