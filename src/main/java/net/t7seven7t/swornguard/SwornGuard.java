/**
 * Copyright (C) 2012 t7seven7t
 */
package net.t7seven7t.swornguard;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;

import lombok.Getter;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.event.Listener;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.t7seven7t.swornguard.commands.*;
import net.t7seven7t.swornguard.commands.jail.*;
import net.t7seven7t.swornguard.commands.patrol.*;
import net.t7seven7t.swornguard.detectors.*;
import net.t7seven7t.swornguard.io.PlayerDataCache;
import net.t7seven7t.swornguard.io.PlayerDataServiceProvider;
import net.t7seven7t.util.ResourceHandler;
import net.t7seven7t.swornguard.listeners.*;
import net.t7seven7t.swornguard.permissions.PermissionHandler;
import net.t7seven7t.swornguard.types.ServerData;
import net.t7seven7t.util.LogHandler;
import net.t7seven7t.util.SimpleVector;

/**
 * @author t7seven7t
 */
public class SwornGuard extends JavaPlugin {
	private @Getter LogHandler logHandler;
	private @Getter CommandHandler commandHandler;
	private @Getter PermissionHandler permissionHandler;
	private @Getter ResourceHandler resourceHandler;
	private @Getter PlayerDataCache playerDataCache;
	private @Getter ServerData serverData;
	
	private @Getter CheatHandler cheatHandler;
	private @Getter AutoModerator autoModerator;
	private @Getter PatrolHandler patrolHandler;
	private @Getter JailHandler jailHandler;
	
	private @Getter AutoClickerDetector autoClickerDetector;
	private @Getter CombatLogDetector combatLogDetector;
	private @Getter CommandDetector commandDetector;
	private @Getter FactionBetrayalDetector factionBetrayaldetector;
	private @Getter FlyDetector flyDetector;
	private @Getter XrayDetector xrayDetector;
	
	private @Getter boolean debug;
	
	private List<Listener> listeners;

	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();
		
		ConfigurationSerialization.registerClass(SimpleVector.class);
		logHandler = new LogHandler(this);
		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler(this);
		resourceHandler = new ResourceHandler(this, this.getClassLoader());
		listeners = new ArrayList<Listener>();
		
		if (!getDataFolder().exists())
			getDataFolder().mkdir();
		
		saveResource("messages.properties", true);
		
		saveDefaultConfig();
		reloadConfig();
		
		playerDataCache = new PlayerDataCache(this);
		getServer().getServicesManager().register(PlayerDataServiceProvider.class, playerDataCache, this, ServicePriority.Normal);		
		serverData = new ServerData(this);
		
		cheatHandler = new CheatHandler(this);
		autoModerator = new AutoModerator(this);
		patrolHandler = new PatrolHandler(this);
		jailHandler = new JailHandler(this);
		
		commandDetector = new CommandDetector(this);
		
		if (getConfig().getBoolean("autoclickerDetectorEnabled"))
			autoClickerDetector = new AutoClickerDetector(this);
		if (getConfig().getBoolean("combatLogDetectorEnabled"))
			combatLogDetector = new CombatLogDetector(this);
		if (getConfig().getBoolean("factionBetrayalDetectorEnabled"))
			factionBetrayaldetector = new FactionBetrayalDetector(this);
		if (getConfig().getBoolean("flyDetectorEnabled"))
			flyDetector = new FlyDetector(this);
		if (getConfig().getBoolean("spamDetectorEnabled"))
			new SpamDetector.SpamOptions(this);
		if (getConfig().getBoolean("xrayDetectorEnabled"))
			xrayDetector = new XrayDetector(this);
		
		registerListener(new BlockListener(this));
		registerListener(new ChatListener(this));
		registerListener(new EntityListener(this));
		registerListener(new PlayerListener(this));
		registerListener(new ServerListener(this));
		registerListener(new FactionsListener(this));
		
		// dmulloy2 new method(s)
		class AutoSaveThread extends BukkitRunnable {
			@Override
			public void run() {
				playerDataCache.save();
			}
		}
		
		if (getConfig().getBoolean("autosave.enabled", true)) {
			int interval = 20 * 60 * getConfig().getInt("autosave.interval", 15);
			new AutoSaveThread().runTaskTimer(this, interval, interval);
		}
		
		debug = getConfig().getBoolean("debug");
		
		commandHandler.setCommandPrefix("sg");
		commandHandler.registerPrefixedCommand(new CmdBanInfo(this));
		commandHandler.registerPrefixedCommand(new CmdHelp(this));
		commandHandler.registerPrefixedCommand(new CmdInfo(this));
		commandHandler.registerPrefixedCommand(new CmdIP(this));
		commandHandler.registerPrefixedCommand(new CmdLegit(this));
		commandHandler.registerPrefixedCommand(new CmdNote(this));
		commandHandler.registerPrefixedCommand(new CmdRatio(this));
		commandHandler.registerPrefixedCommand(new CmdReload(this));
		commandHandler.registerPrefixedCommand(new CmdShow(this));
		commandHandler.registerPrefixedCommand(new CmdFHistory(this));
		commandHandler.registerPrefixedCommand(new CmdSInfo(this));
		
		commandHandler.registerCommand(new CmdAutoPatrol(this));
		commandHandler.registerCommand(new CmdCheatTeleport(this));
		commandHandler.registerCommand(new CmdPatrol(this));
		commandHandler.registerCommand(new CmdVanish(this));
		commandHandler.registerCommand(new CmdVanishList(this));
		
		commandHandler.registerCommand(new CmdCheck(this));
		commandHandler.registerCommand(new CmdJail(this));
		commandHandler.registerCommand(new CmdJailHelp(this));
		commandHandler.registerCommand(new CmdMute(this));
		commandHandler.registerCommand(new CmdReason(this));
		commandHandler.registerCommand(new CmdSet(this));
		commandHandler.registerCommand(new CmdStatus(this));
		commandHandler.registerCommand(new CmdTime(this));
		commandHandler.registerCommand(new CmdUnjail(this));
		
		commandHandler.registerCommand(new CmdTrollHell(this));
		
		long finish = System.currentTimeMillis();
		
		logHandler.log("{0} has been enabled ({1}ms)", getDescription().getFullName(), finish-start);
	}

	@Override
	public void onDisable() {		
		long start = System.currentTimeMillis();
		
		playerDataCache.save();
		jailHandler.saveJail();
		
		getServer().getScheduler().cancelTasks(this);
		
		long finish = System.currentTimeMillis();
		
		logHandler.log("{0} has been disabled ({1}ms)", getDescription().getFullName(), finish-start);
	}
	
	public void registerListener(Listener listener) {
		listeners.add(listener);
		getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public String getMessage(String string) {
		try {
			return resourceHandler.getMessages().getString(string);
		} catch (MissingResourceException ex) {
			logHandler.log(Level.WARNING, "Messages locale is missing key for: {0}", string);
			return null;
		}
	}
}