package com.minesworn.swornguard.commands;

import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.SwornGuard;

public class CmdReload extends SGCommand {

	public CmdReload() {
		this.name = "reload";
		this.aliases.add("r");
		this.description = "Does not yet safely reload the server. Used for debugging purposes only.";
		this.permission = Permission.RELOAD.node;
		this.mustBePlayer = false;
	}
	
	@Override
	public void perform() {
		SwornGuard.p.reload();
		confirmMessage(SwornGuard.lang.getMessage("confirmreload"));
		SwornGuard.log(SwornGuard.lang.getMessage("logreload"), sender.getName());
	}

}
