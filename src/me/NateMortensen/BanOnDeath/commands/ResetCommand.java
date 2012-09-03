package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BODPlayer;
import me.NateMortensen.BanOnDeath.BanOnDeath;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class ResetCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You must specify a player to reset.");
            return;
        }
        BODPlayer bplayer = plugin.getPlayer(args[0]);
        //Reset their lives if they're online, as you can only do that when you can get their permissions.
        Player p = Bukkit.getServer().getPlayer(args[0]);
        if (p != null && p.isOnline()){
        	bplayer.reset(plugin.getTierOfPlayer(p));
        	p.sendMessage("Your lives have been reset!");
        	sender.sendMessage("You reset the lives and ban information of "+bplayer.getName());
        	return;
        }
        //else
        //Reset their lives when they next come online.
        bplayer.setNeedsReset(true);
        sender.sendMessage("You reset the lives and ban information of "+bplayer.getName());
        plugin.getServer().dispatchCommand(sender, "bod info "+bplayer.getName());
    }

    public String getPermissionNode() {
        return "bod.reset";
    }

    public String[] getNames() {
        return new String[]{"reset"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
