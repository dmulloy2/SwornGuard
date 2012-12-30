/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard.io;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import net.t7seven7t.swornguard.SwornGuard;

/**
 * @author t7seven7t
 */
public class ResourceHandler {
	private ResourceBundle messages;
	
	public ResourceHandler(SwornGuard plugin, ClassLoader classLoader) {
		try {
			messages = ResourceBundle.getBundle("messages", Locale.getDefault(), new FileResourceLoader(classLoader, plugin));
		} catch (MissingResourceException ex) {
			plugin.getLogHandler().log(Level.SEVERE, "Could not find resource bundle: messages.properties");
		}
	}
	
	public ResourceBundle getMessages() {
		return messages;
	}

}
