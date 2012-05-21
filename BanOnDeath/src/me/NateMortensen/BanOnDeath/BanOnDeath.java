package me.NateMortensen.BanOnDeath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.logging.Logger;
import me.NateMortensen.BanOnDeath.commands.BODCommand;
import me.NateMortensen.BanOnDeath.commands.BODCommandDispatcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
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
    public PlayerManager playermanager;
    public String kickmessage;
    public BODCommandDispatcher dispatcher;

    @Override
    public void onEnable() {
        config = getConfig();
        final Logger log = getLogger();
        //Initialize classes.
        playermanager = new PlayerManager(this);
        listener = new BodListener(this);
        dispatcher = new BODCommandDispatcher(this);
        //Set the values of any variables.
        logToFile = config.getBoolean("writeToFile", true);
        kickmessage = config.getString("kick_message", "You have failed!");
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
    }

    @Override
    public void onDisable() {
        playermanager.savePlayers();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("lives")) {
            if (sender instanceof Player) {
                sender.sendMessage("You have " + playermanager.getLives((Player)sender) + " lives remaining.");
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
            sender.sendMessage(cmd.getPermissionMessage());
        } else {
            sender.sendMessage("Try one of the following commands:");
            sender.sendMessage(availableCommands.toArray(new String[availableCommands.size()]));
        }
    }
    public BODCommand getSubCommand(final String str) {
        return dispatcher.getCommand(str);
    }


}
