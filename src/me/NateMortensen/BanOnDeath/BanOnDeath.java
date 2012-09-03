package me.NateMortensen.BanOnDeath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;
import me.NateMortensen.BanOnDeath.commands.BODCommand;
import me.NateMortensen.BanOnDeath.commands.BODCommandDispatcher;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ***
 *
 * @author Nate Mortensen
 */
public class BanOnDeath extends JavaPlugin {

	BodListener listener;
	public FileConfiguration config;
	public File file;
	public boolean logToFile;
	public boolean logging;
	public List<Tier> tiers = new ArrayList<Tier>();
	public List<BODPlayer> players = new ArrayList<BODPlayer>();
	public String kickmessage;
	public BODCommandDispatcher dispatcher;
	public FileConfiguration playersconfig;
	public FileConfiguration tiersconfig;
	public static Tier defaulttier;
	public static BanOnDeath main;
	Logger log;

	@Override
	public void onEnable() {
		main = this;
		config = getConfig();
		log = getLogger();
		//Initialize classes.
		listener = new BodListener();
		dispatcher = new BODCommandDispatcher(this);
		//send the "enabled" message.
		PluginDescriptionFile pdf = this.getDescription();
		String name = pdf.getName();
		String version = pdf.getVersion();
		List<String> author = pdf.getAuthors();
		players = new ArrayList<BODPlayer>();
		playersconfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "players.yml"));
		log.info(name +" v" + version + " by " + author.get(0) + " enabled!");
		//Write To file.
		loadConfig();
		loadTiers();
		if (logToFile) {
			file = new File(getDataFolder(), "banlist.csv");
			if (!file.exists()) {
				try {
					file.createNewFile(); 
					PrintWriter writer = new PrintWriter(new FileWriter(file.getPath()));
					writer.println("Username,Date,Unban Date,Cause");
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	public static BanOnDeath getInstance(){
		return main;
	}
	public static Tier getDeafultTier(){
		return defaulttier;
	}
	public void loadConfig(){
		reloadConfig();
		//Config check for setting the values of defaults.
		YAPI.configCheck(config, "logging", true);
		YAPI.configCheck(config, "writeToFile", true);
		//Set the values of any variables.
		logging = config.getBoolean("logging", true);
		logToFile = config.getBoolean("writeToFile", true);
		saveConfig();
	}
	public void loadTiers(){
		tiers.clear();
		tiersconfig = null;
		tiersconfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "tiers.yml"));
		if (!tiersconfig.contains("default"))
			tiersconfig.createSection("default");
		defaulttier = new DefaultTier(tiersconfig.getConfigurationSection("default"));
		for (String string : tiersconfig.getKeys(false))
			tiers.add(new BODTier(tiersconfig.getConfigurationSection(string)));
		tiers.remove(getTier("default"));
		for (Tier tier : tiers)
			((BODTier)tier).initialize();
		try {
			tiersconfig.save(new File(getDataFolder(), "tiers.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public BODPlayer getPlayer(String name){
		name = name.toLowerCase();
		//First traverse the list of players and return a player if the name matches.
		for (BODPlayer player : players){
			if (player.getName().equals(name)) return player;
		}
		//Create a new player if one wasn't found.
		//loadPlayerToList(name);   We only want to load players to the list if they're online. 
		//and if they're already online, they would have been added on login.
		return loadPlayer(name);
	}
	public void loadPlayerToList(String name){
		name = name.toLowerCase();
		players.add(loadPlayer(name));
	}
	public BODPlayer loadPlayer(String name){
		name = name.toLowerCase();
		return new BODPlayer(playersconfig, name, this);
	}
	public Tier getTierOfPlayer(Player player){
		for (Tier tier : tiers){
			if (player.hasPermission("bod.tiers."+tier.getName().toLowerCase()))return tier;
		}
		return defaulttier;
	}
	public Tier getTier(String name){
		name = name.toLowerCase();
		for (Tier tier : tiers){
			if (tier.getName().equals(name)) return tier;
		}
		return defaulttier;
	}
	public void log(String message){
		if (logging) log.info(message);
	}

	@Override
	public void onDisable() {
		YAPI.saveYaml(this, playersconfig, "players.yml");
		main = null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("lives")) {
			if (sender instanceof Player) {
				BODPlayer player = getPlayer(sender.getName());
				sender.sendMessage("You have " + Integer.toString(player.getLives()) +  (player.getLives() == 1 ? " life" : " lives") +" remaining.");
			} else {
				sender.sendMessage("Last time I checked Consoles can't die, and don't have lives.  Might just be me though.");
				sender.sendMessage("If you're trying to get info on a player, use /bod info <playername> instead.");
			}
		} else if (cmd.getName().equalsIgnoreCase("bod")) {
			if (!dispatcher.dispatch(sender, args)) {
				showAvailableCommands(sender, cmd);
			}
		}
		return true;
	}
	private void showAvailableCommands(CommandSender sender, Command cmd) {
		final List<String> availableCommands = new ArrayList<String>();
		for (final BODCommand command : dispatcher.getCommands()) {
			if (command.getPermissionNode() == null || sender.hasPermission(command.getPermissionNode())) {
				availableCommands.add(BODCommandDispatcher.getFullSyntax(command));
			}
		}
		if (availableCommands.isEmpty()) {
			sender.sendMessage(ChatColor.RED+"You don't have permission.");
		} else {
			sender.sendMessage("Try one of the following commands:");
			sender.sendMessage(availableCommands.toArray(new String[availableCommands.size()]));
		}
	}
	public BODCommand getSubCommand(final String str) {
		return dispatcher.getCommand(str);
	}


}
