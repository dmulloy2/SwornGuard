/**
 * SwornGuard - a bukkit plugin
 * Copyright (C) 2012 - 2014 MineSworn and Affiliates
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.t7seven7t.swornguard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import net.dmulloy2.SwornAPI;
import net.dmulloy2.SwornPlugin;
import net.dmulloy2.commands.CmdHelp;
import net.dmulloy2.handlers.CommandHandler;
import net.dmulloy2.handlers.LogHandler;
import net.dmulloy2.handlers.PermissionHandler;
import net.dmulloy2.handlers.ResourceHandler;
import net.dmulloy2.types.Reloadable;
import net.t7seven7t.swornguard.commands.CmdAlt;
import net.t7seven7t.swornguard.commands.CmdBanInfo;
import net.t7seven7t.swornguard.commands.CmdFHistory;
import net.t7seven7t.swornguard.commands.CmdIP;
import net.t7seven7t.swornguard.commands.CmdInfo;
import net.t7seven7t.swornguard.commands.CmdLeaderboard;
import net.t7seven7t.swornguard.commands.CmdLegit;
import net.t7seven7t.swornguard.commands.CmdNote;
import net.t7seven7t.swornguard.commands.CmdRatio;
import net.t7seven7t.swornguard.commands.CmdReload;
import net.t7seven7t.swornguard.commands.CmdSInfo;
import net.t7seven7t.swornguard.commands.CmdShow;
import net.t7seven7t.swornguard.commands.CmdVersion;
import net.t7seven7t.swornguard.commands.jail.CmdCheck;
import net.t7seven7t.swornguard.commands.jail.CmdJail;
import net.t7seven7t.swornguard.commands.jail.CmdJailHelp;
import net.t7seven7t.swornguard.commands.jail.CmdReason;
import net.t7seven7t.swornguard.commands.jail.CmdSet;
import net.t7seven7t.swornguard.commands.jail.CmdStatus;
import net.t7seven7t.swornguard.commands.jail.CmdTime;
import net.t7seven7t.swornguard.commands.jail.CmdUnjail;
import net.t7seven7t.swornguard.commands.patrol.CmdAutoPatrol;
import net.t7seven7t.swornguard.commands.patrol.CmdCheatTeleport;
import net.t7seven7t.swornguard.commands.patrol.CmdPatrol;
import net.t7seven7t.swornguard.commands.patrol.CmdVanish;
import net.t7seven7t.swornguard.commands.patrol.CmdVanishList;
import net.t7seven7t.swornguard.commands.troll.CmdTrollBan;
import net.t7seven7t.swornguard.commands.troll.CmdTrollCheck;
import net.t7seven7t.swornguard.commands.troll.CmdTrollHell;
import net.t7seven7t.swornguard.commands.troll.CmdTrollMute;
import net.t7seven7t.swornguard.detectors.AutoClickerDetector;
import net.t7seven7t.swornguard.detectors.CombatLogDetector;
import net.t7seven7t.swornguard.detectors.CommandDetector;
import net.t7seven7t.swornguard.detectors.FactionBetrayalDetector;
import net.t7seven7t.swornguard.detectors.FlyDetector;
import net.t7seven7t.swornguard.detectors.SpamDetector;
import net.t7seven7t.swornguard.detectors.XrayDetector;
import net.t7seven7t.swornguard.handlers.AutoModerator;
import net.t7seven7t.swornguard.handlers.CheatHandler;
import net.t7seven7t.swornguard.handlers.JailHandler;
import net.t7seven7t.swornguard.handlers.LogFilterHandler;
import net.t7seven7t.swornguard.handlers.PatrolHandler;
import net.t7seven7t.swornguard.handlers.TrollHandler;
import net.t7seven7t.swornguard.integration.EssentialsHandler;
import net.t7seven7t.swornguard.io.PlayerDataCache;
import net.t7seven7t.swornguard.io.PlayerDataServiceProvider;
import net.t7seven7t.swornguard.listeners.BlockListener;
import net.t7seven7t.swornguard.listeners.ChatListener;
import net.t7seven7t.swornguard.listeners.EntityListener;
import net.t7seven7t.swornguard.listeners.FactionsListener;
import net.t7seven7t.swornguard.listeners.PlayerListener;
import net.t7seven7t.swornguard.listeners.ServerListener;
import net.t7seven7t.swornguard.types.Preconditions;
import net.t7seven7t.swornguard.types.ServerData;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author t7seven7t
 */
public class SwornGuard extends SwornPlugin {
	private @Getter ResourceHandler resourceHandler;
	private @Getter PlayerDataCache playerDataCache;
	private @Getter Preconditions preconditions;
	private @Getter TrollHandler trollHandler;
	private @Getter ServerData serverData;

	private @Getter CheatHandler cheatHandler;
	private @Getter AutoModerator autoModerator;
	private @Getter PatrolHandler patrolHandler;
	private @Getter JailHandler jailHandler;
	private @Getter LogFilterHandler logFilterHandler;

	private @Getter AutoClickerDetector autoClickerDetector;
	private @Getter CombatLogDetector combatLogDetector;
	private @Getter CommandDetector commandDetector;
	private @Getter FactionBetrayalDetector factionBetrayalDetector;
	private @Getter FlyDetector flyDetector;
	private @Getter XrayDetector xrayDetector;

	private @Getter EssentialsHandler essentialsHandler;

	private List<Listener> listeners;

	@Override
	public void onLoad() {
		SwornAPI.checkRegistrations();
	}

	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();

		logHandler = new LogHandler(this);

		saveDefaultConfig();
		reloadConfig();

		commandHandler = new CommandHandler(this);
		permissionHandler = new PermissionHandler(this);

		File messages = new File(getDataFolder(), "messages.properties");
		if (messages.exists())
			messages.delete();

		resourceHandler = new ResourceHandler(this, this.getClassLoader());

		playerDataCache = new PlayerDataCache(this);
		getServer().getServicesManager().register(PlayerDataServiceProvider.class, playerDataCache, this, ServicePriority.Normal);

		serverData = new ServerData(this);
		preconditions = new Preconditions(this);
		trollHandler = new TrollHandler(this);
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
			factionBetrayalDetector = new FactionBetrayalDetector(this);
		if (getConfig().getBoolean("flyDetectorEnabled"))
			flyDetector = new FlyDetector(this);
		if (getConfig().getBoolean("spamDetectorEnabled"))
			new SpamDetector.SpamOptions(this);
		if (getConfig().getBoolean("xrayDetectorEnabled"))
			xrayDetector = new XrayDetector(this);

		logFilterHandler = new LogFilterHandler(this);

		listeners = new ArrayList<Listener>();
		registerListener(new BlockListener(this));
		registerListener(new ChatListener(this));
		registerListener(new EntityListener(this));
		registerListener(new PlayerListener(this));
		registerListener(new ServerListener(this));

		setupIntegration();

		if (getConfig().getBoolean("autosave.enabled", true)) {
			int interval = 20 * 60 * getConfig().getInt("autosave.interval", 15);

			new BukkitRunnable() {

				@Override
				public void run() {
					playerDataCache.save();
					playerDataCache.cleanupData();
				}

			}.runTaskTimerAsynchronously(this, interval, interval);
		}

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
		commandHandler.registerPrefixedCommand(new CmdVersion(this));

		commandHandler.registerCommand(new CmdAlt(this));
		commandHandler.registerCommand(new CmdAutoPatrol(this));
		commandHandler.registerCommand(new CmdCheatTeleport(this));
		commandHandler.registerCommand(new CmdPatrol(this));
		commandHandler.registerCommand(new CmdVanish(this));
		commandHandler.registerCommand(new CmdVanishList(this));
		commandHandler.registerCommand(new CmdLeaderboard(this));

		commandHandler.registerCommand(new CmdCheck(this));
		commandHandler.registerCommand(new CmdJail(this));
		commandHandler.registerCommand(new CmdJailHelp(this));
		commandHandler.registerCommand(new CmdReason(this));
		commandHandler.registerCommand(new CmdSet(this));
		commandHandler.registerCommand(new CmdStatus(this));
		commandHandler.registerCommand(new CmdTime(this));
		commandHandler.registerCommand(new CmdUnjail(this));

		commandHandler.registerCommand(new CmdTrollBan(this));
		commandHandler.registerCommand(new CmdTrollHell(this));
		commandHandler.registerCommand(new CmdTrollMute(this));
		commandHandler.registerCommand(new CmdTrollCheck(this));

		logHandler.log("{0} has been enabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	@Override
	public void onDisable() {
		long start = System.currentTimeMillis();

		playerDataCache.save();
		jailHandler.saveJail();

		getServer().getScheduler().cancelTasks(this);
		getServer().getServicesManager().unregisterAll(this);

		logHandler.log("{0} has been disabled. Took {1} ms.", getDescription().getFullName(), System.currentTimeMillis() - start);
	}

	public void registerListener(Listener listener) {
		listeners.add(listener);

		getServer().getPluginManager().registerEvents(listener, this);
	}

	// ---- Integration

	private void setupIntegration() {
		// Factions listener
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("SwornNations") || pm.isPluginEnabled("Factions")) {
			registerListener(new FactionsListener(this));
		}

		try {
			// Essentials integration
			essentialsHandler = new EssentialsHandler(this);
		} catch (Throwable ex) { }
	}

	public boolean isEssentialsEnabled() {
		return essentialsHandler != null && essentialsHandler.isEnabled();
	}

	@Override
	public void reload() {
		// Config
		reloadConfig();

		// Handler(s)
		logFilterHandler.reload();

		// Listeners
		for (Listener listener : listeners) {
			if (listener instanceof Reloadable) {
				((Reloadable) listener).reload();
			}
		}
	}

	public String getMessage(String string) {
		return resourceHandler.getMessage(string);
	}
}