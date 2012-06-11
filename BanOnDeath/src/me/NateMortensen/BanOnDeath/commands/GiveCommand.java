package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BODPlayer;
import me.NateMortensen.BanOnDeath.BanOnDeath;
import org.bukkit.command.CommandSender;

/**
 * Command to give a player lives.
 *
 * @author Nate Mortensen
 * @author Score_Under
 */
public class GiveCommand implements BODCommand {

    public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("Try " + BODCommandDispatcher.getFullSyntax(this) + " instead.");
            return;
        }
        final int amount;
        try {
            amount = Integer.parseInt(args[1]);
            if (amount <= 0)
            {
                throw new NumberFormatException("Amount must be positive.");
            }
        } catch (final NumberFormatException e) {
            sender.sendMessage("The amount of lives given must be positive. (Got \"" + args[1] + "\")");
            sender.sendMessage("Try " + BODCommandDispatcher.getFullSyntax(this) + " instead.");
            return;
        }
        final String origPlayerName = args[0];
        final String lowerPlayerName = origPlayerName.toLowerCase();
        final BODPlayer player = plugin.getPlayer(lowerPlayerName);
        final int oldLives = player.getLives();
        final int newLives = oldLives + amount;
        if (newLives <= 0) {
            sender.sendMessage("That would leave the player with less than 0 lives.  If you want to ban them, use " + BODCommandDispatcher.getFullSyntax(plugin.getSubCommand("ban")));
            sender.sendMessage("Currently, that player has " + oldLives + (oldLives == 1 ? " life" : " lives") + " remaining.");
        } else {
            player.setLives(newLives);
            sender.sendMessage(origPlayerName + " has been moved up from " + oldLives + " to " + newLives + (newLives == 1 ? " life" : " lives") + ".");
        }
    }

    public String getPermissionNode() {
        return "bod.give";
    }

    public String[] getNames() {
        return new String[]{"give", "givelives", "addlives"};
    }

    public String getSyntax() {
        return "<player> <lives to give>";
    }
}
