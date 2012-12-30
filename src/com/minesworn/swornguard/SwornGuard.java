package com.minesworn.swornguard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.minesworn.swornguard.PermissionsManager.Permission;
import com.minesworn.swornguard.commands.SGCommandRoot;
import com.minesworn.swornguard.core.SPlugin;
import com.minesworn.swornguard.core.commands.SCommand;
import com.minesworn.swornguard.core.commands.SCommandRoot;
import com.minesworn.swornguard.core.util.Util;
import com.minesworn.swornguard.detectors.FlyDetector;
import com.minesworn.swornguard.events.CheatEvent;
import com.minesworn.swornguard.lang.Lang;
import com.minesworn.swornguard.listeners.BlockListener;
import com.minesworn.swornguard.listeners.ChatListener;
import com.minesworn.swornguard.listeners.EntityListener;
import com.minesworn.swornguard.listeners.JailListener;
import com.minesworn.swornguard.listeners.NationsListener;
import com.minesworn.swornguard.listeners.PlayerJoinQuitListener;
import com.minesworn.swornguard.patrol.Patrol;
import com.minesworn.swornguard.patrol.commands.SPCommandRoot;
import com.minesworn.swornguard.threads.SaveRunnable;

public class SwornGuard extends SPlugin {
	public static PlayerDatabase playerdatabase;
	public static ServerInfo serverInfo;
	public static boolean autoModBotEnabled = false;
	public static SCommandRoot<?> spCommandRoot;
	
	@Override
	public void onEnable() {
		preEnable();
		lang = new Lang();
		Lang.load();
		Config.load();
		playerdatabase = new PlayerDatabase();
		serverInfo = new ServerInfo();
		commandRoot = new SGCommandRoot();
		spCommandRoot = new SPCommandRoot();
		
		new SaveRunnable();
		
		if (Config.enableFlyDetector)
			new FlyDetector();
		
		registerEvents();
		
		autoModBotEnabled = Config.autoModeratorAlwaysEnabled;
	}
	
	public void onDisable() {
		preDisable();
		playerdatabase.save();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (!super.onCommand(sender, cmd, lbl, args, false)) {
			for (SCommand<?> command : spCommandRoot.commands) {	
				if (cmd.getName().equalsIgnoreCase(command.getName()) || command.getAliases().contains(cmd.getName().toLowerCase())) {							
					command.execute(sender, args);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public void afterReload() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			playerdatabase.addPlayer(p.getName());
		}
	}
	
	void registerEvents() {
		Bukkit.getPluginManager().registerEvents(new PlayerJoinQuitListener(), p);
		Bukkit.getPluginManager().registerEvents(new BlockListener(), p);
		Bukkit.getPluginManager().registerEvents(new ChatListener(), p);
		Bukkit.getPluginManager().registerEvents(new EntityListener(), p);
		if (isPluginEnabled("SwornJail"))
			Bukkit.getPluginManager().registerEvents(new JailListener(), p);
		if (Config.enableFactionBetrayalDetector && (isPluginEnabled("SwornNations") || isPluginEnabled("Factions")))
			Bukkit.getPluginManager().registerEvents(new NationsListener(), p);
	}
	
	public static void announceCheat(CheatEvent e) {
		PlayerInfo i = playerdatabase.getPlayer(e.getPlayer().getName());
		Bukkit.getPluginManager().callEvent(e);
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (PermissionsManager.hasPermission(p, Permission.CAN_SEE_CHEAT_REPORTS.node)) {
				p.sendMessage(ChatColor.RED + e.getMessage());
				if (i.getJailCount() >= Config.jailsBeforeNotice && Config.jailAmountNoticeEnabled)
					p.sendMessage(ChatColor.RED + "Player " + e.getPlayer().getName() + " has multiple offences they have been jailed for. Please investigate this notice.");
			}
		}
		
		if ((AutoModerator.isOnlyModeratorOnline() || autoModBotEnabled) && Config.enableAutoModeratorBot)
			AutoModerator.manageCheatEvent(e);
		
		if (e.getType() != Cheat.XRAY || (System.currentTimeMillis() - i.getLastWarnedForXray() > 432000000L)) {
			i.getProfilerList().add("[" + Util.getLongDateCurr() + " GMT] " + ChatColor.RED + "pinged the cheat detector for: " + ChatColor.GOLD + e.getType().toString());
		} 
		
		if (e.getType().equals(Cheat.SPAM) && !AutoModerator.isOnlyModeratorOnline()) {
			AutoModerator.manageCheatEvent(e);
		}
		
		if (e.getType().equals(Cheat.FLYING) || e.getType().equals(Cheat.XRAY)) {
			Patrol.addCheater(e.getPlayer());
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission(Permission.RESPOND_CHEAT_DETECTOR.node))
					player.sendMessage(ChatColor.RED + "To respond to this cheat alert use /ctp " + e.getPlayer().getName());
			}
		}
		
		log("Cheatevent player: " + e.getPlayer().getName());
		log("Cheatevent type: " + e.getType().toString());
	}
	
//	public static void warnAltAccount(String player1, String player2) {
//		for (Player p : Bukkit.getOnlinePlayers()) {
//			if (PermissionsManager.hasPermission(p, Permission.CAN_SEE_CHEAT_REPORTS.node))
//				p.sendMessage(	ChatColor.RED + "[WARNING] " +
//								ChatColor.YELLOW + player1 +
//								ChatColor.BLUE + " has possible alt: " +
//								ChatColor.YELLOW + player2);
//		}
//	}
	
}
