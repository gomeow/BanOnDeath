package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BODPlayer;
import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Unban a player from this plugin.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class UnbanCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("You must specify a player to unban.");
            return;
        }
        final String targetPlayerName = args[0].toLowerCase();
        BODPlayer player = plugin.getPlayer(targetPlayerName);

        player.setBanTime(0);
        player.setNeedsReset(true);
        sender.sendMessage(targetPlayerName + " has been unbanned.");
    }

    public String getPermissionNode() {
        return "bod.unban";
    }

    public String[] getNames() {
        return new String[]{"unban", "pardon"};
    }

    public String getSyntax() {
        return "<player>";
    }
}
