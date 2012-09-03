/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.NateMortensen.BanOnDeath.commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.NateMortensen.BanOnDeath.BODPlayer;
import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Command to get information on a player. Shows unban date if applicable, or
 * lives remaining and if applicable.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class InfoCommand implements BODCommand {
    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You must specify a player to get info on.");
            return;
        }
        final String targetPlayerName = args[0].toLowerCase();
        BODPlayer player = plugin.getPlayer(targetPlayerName);
        String lives;
        String isbanned;
        String unbantime;
        String message;
        //set lives.
        lives = Integer.toString(player.getLives());
        //set isbanned.
        if (player.isBanned()){
        	isbanned = "currently banned";
        }else {
        	isbanned = "not currently banned by BOD";
        }
        //set unbantime
        unbantime = "";
        if (player.getNeedsReset()){
        	sender.sendMessage("Specified player's information will be reset upon their next login.");
        	return;
        }
        if (player.isBanned()){
        	unbantime = ", and will be unbanned on "+dateFormatter.format(new Date(player.getUnbanDate()));
        }
        if (unbantime.equals("")){
        	message = args[0] + " has "+lives+" lives, and is "+isbanned;
        }
        else{
        	message = args[0] + " has "+lives+" lives, is "+isbanned+" "+unbantime+".";
        }
        sender.sendMessage(message);
    }

    public String getPermissionNode() {
        return "bod.info";
    }

    public String[] getNames() {
        return new String[]{"info"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
