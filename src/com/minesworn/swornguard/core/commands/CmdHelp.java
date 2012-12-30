package com.minesworn.swornguard.core.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;

import com.minesworn.swornguard.core.SPlugin;

public class CmdHelp extends SCommand<SPlugin> {

	public CmdHelp() {
		this.name = "help";
		this.description = "Shows " + SPlugin.p.getName() + " help.";
		this.permission = "";
	}
	
	@Override
	public void perform() {
		for (String s : getPageLines()) {
			msg(s);
		}		
	}

	public ArrayList<String> getPageLines() {
		ArrayList<String> pageLines = new ArrayList<String>();
		pageLines.add(ChatColor.GOLD + SPlugin.p.getName() + " Help:");
		
		for (SCommand<?> command : SPlugin.commandRoot.commands) {
			if (sender.hasPermission(command.permission))
				pageLines.add(command.getUsageTemplate(true));
		}
		
		return pageLines;
	}
	
}
