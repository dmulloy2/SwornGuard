package com.minesworn.swornguard;

import com.minesworn.swornguard.core.permissions.PermissionBase;

public class PermissionsManager extends PermissionBase {
	
	public enum Permission {
		INFO("info"),
		INFO_SEE_OTHERS("info.seeothers"),
		CAN_BAN("canban"),
		CAN_FLY("canfly"),
		CAN_SPAM("canspam"),
		CAN_USE_BLOCKED_CMDS("canuseblockedcommands"),
		CAN_SEE_CHEAT_REPORTS("canseecheatreports"),
		CAN_CHECK_IPS("cancheckips"),
		BAN_INFO("baninfo"),
		SHOW("show"),
		NOTE("note"), 
		CAN_KICK("cankick"), 
		MOD_BOT("modbot"), 
		RATIO("ratio"), 
		LEGIT("legit"), 
		SINFO("sinfo"),
		RELOAD("reload"),
		PATROL("patrol"),
		AUTOPATROL("autopatrol"),
		VANISH("vanish"),
		VANISHLIST("vanishlist"),
		NOT_PATROLLED("notpatrolled"),
		CAN_SEE_VANISHED("canseevanished"), 
		RESPOND_CHEAT_DETECTOR("ctp");
		
		public final String node;
		Permission(final String node) {
			this.node = (SwornGuard.p.getName() + "." + node).toLowerCase();
		}
		
	}
	
}
