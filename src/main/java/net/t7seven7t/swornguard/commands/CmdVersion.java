/**
 * (c) 2014 dmulloy2
 */
package net.t7seven7t.swornguard.commands;

import net.dmulloy2.types.StringJoiner;
import net.t7seven7t.swornguard.SwornGuard;

/**
 * @author dmulloy2
 */

public class CmdVersion extends SwornGuardCommand {

	public CmdVersion(SwornGuard plugin) {
		super(plugin);
		this.name = "version";
		this.aliases.add("v");
		this.description = "Displays version info";
	}

	@Override
	public void perform() {
		sendMessage("&3====[ &eSwornGuard &3]====");
		sendMessage("&bVersion&e: {0}", plugin.getDescription().getVersion());
		sendMessage("&bAuthors&e: {0}", new StringJoiner("&b, &e").appendAll(plugin.getDescription().getAuthors()));
		sendMessage("&bIssues&e: https://github.com/MineSworn/SwornGuard/issues");
	}

}
