/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.handlers;

import java.util.logging.Level;

import net.t7seven7t.util.FormatUtil;

import org.bukkit.plugin.java.JavaPlugin;


/**
 * @author t7seven7t
 */
public class LogHandler {
	private final JavaPlugin plugin;
	
	public LogHandler(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	public final void log(Level level, String msg, Object... objects) {
		plugin.getServer().getLogger().log(level, FormatUtil.format("[{0}] {1}", plugin.getName(), FormatUtil.format(msg, objects)));		
	}

	public final void log(String msg, Object... objects) {
		log(Level.INFO, msg, objects);
	}

}