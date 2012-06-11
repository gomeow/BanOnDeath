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
    public List<BODTier> tiers;
    public List<BODPlayer> players;
    public String kickmessage;
    public BODCommandDispatcher dispatcher;
    public FileConfiguration playersconfig;
    public FileConfiguration tiersconfig;
    public BODTier defaulttier;
    Logger log;

    @Override
    public void onEnable() {
        config = getConfig();
        log = getLogger();
        //Initialize classes.
        listener = new BodListener(this);
        dispatcher = new BODCommandDispatcher(this);
        //Set the values of any variables.
        logging = config.getBoolean("logging", true);
        logToFile = config.getBoolean("writeToFile", true);
        kickmessage = config.getString("kick_message", "You have failed!");
        saveConfig();
        //TODO initialize commandexecutor
        //Register listener
        getServer().getPluginManager().registerEvents(listener, this);
        //Register CommandExecutor
        //TODO register CommandExecutor
        //send the "enabled" message.
        PluginDescriptionFile pdf = this.getDescription();
        String name = pdf.getName();
        String version = pdf.getVersion();
        List<String> author = pdf.getAuthors();
        players = new ArrayList<BODPlayer>();
        playersconfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "players.yml"));
        tiersconfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "tiers.yml"));
        log.info(name +" v" + version + " by " + author.get(0) + " enabled!");
        //Write To file.
        if (logToFile) {
            file = new File(getDataFolder() + "/banlist.cvs");
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
        defaulttier = new BODTier(tiersconfig, "default", this);
        tiers = loadTiers();
        
    }
    public List<BODTier> loadTiers(){
    	List<BODTier> loaded = new ArrayList<BODTier>();
    	Set<String> keys = tiersconfig.getKeys(false);
    	List<String> listkeys = new ArrayList<String>();
    	for (String string : keys){
    		listkeys.add(string);
    	}
    	for (String string : listkeys){ 
    		loaded.add(new BODTier(config, string, this));
    	}
    	return loaded;
    }
    public BODPlayer getPlayer(String name){
    	name = name.toLowerCase();
    	//First traverse the list of players and return a player if the name matches.
    	for (BODPlayer player : players){
    		if (player.getName().equals(name)) return player;
    	}
    	//Create a new player if one wasn't found.
    	loadPlayerToList(name);
    	return loadPlayer(name);
    }
    public void loadPlayerToList(String name){
    	name = name.toLowerCase();
    	players.add(loadPlayer(name));
    }
    public void loadTierToList(String name){
    	name = name.toLowerCase();
    	tiers.add(loadTier(name));
    }
    public BODTier loadTier(String name){
    	name = name.toLowerCase();
    	return new BODTier(tiersconfig, name, this);
    }
    public BODPlayer loadPlayer(String name){
    	name = name.toLowerCase();
    	return new BODPlayer(config, name, this);
    }
    public BODTier getTierOfPlayer(Player player){
    	for (BODTier tier : tiers){
    		if (player.hasPermission("banondeath.tiers."+tier.getName()))return tier;
    	}
    	return null;
    }
    public BODTier getTier(String name){
    	name = name.toLowerCase();
    	for (BODTier tier : tiers){
    		if (tier.getName().equals(name)) return tier;
    	}
    	return defaulttier;
    }
    public void log(String message)
    {
    	if (logging) log.info(message);
    }

    @Override
    public void onDisable() {
        for (BODPlayer player : players)
        	player.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lives")) {
            if (sender instanceof Player) {
                sender.sendMessage("You have " + getPlayer(sender.getName()).getLives() + " lives remaining.");
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
