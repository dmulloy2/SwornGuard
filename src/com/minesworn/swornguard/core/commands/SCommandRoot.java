package com.minesworn.swornguard.core.commands;

import java.util.HashSet;
import java.util.Set;

import com.minesworn.swornguard.core.SPlugin;

public class SCommandRoot<S extends SPlugin> {

	public Set<SCommand<?>> commands = new HashSet<SCommand<?>>();
	
	private CmdHelp CMD_HELP = new CmdHelp();
	
	public SCommandRoot() {
		addCommand(CMD_HELP);
	}
	
	public void addCommand(SCommand<?> command) {
		commands.add(command);
	}
	
}
