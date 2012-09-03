/**
 * 
 */
package me.NateMortensen.BanOnDeath.commands;

import me.NateMortensen.BanOnDeath.BODPlayer;
import me.NateMortensen.BanOnDeath.BanOnDeath;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author Nate Mortensen
 *
 */
public class ReloadCommand implements BODCommand{

	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.commands.BODCommand#getPermissionNode()
	 */
	@Override
	public String getPermissionNode() {
		return "bod.reload";
	}

	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.commands.BODCommand#getNames()
	 */
	@Override
	public String[] getNames() {
		return new String[]{"reload"};
	}

	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.commands.BODCommand#getSyntax()
	 */
	@Override
	public String getSyntax() {
		return "reload";
	}

	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.commands.BODCommand#execute(me.NateMortensen.BanOnDeath.BanOnDeath, org.bukkit.command.CommandSender, java.lang.String[])
	 */
	@Override
	public void execute(BanOnDeath plugin, CommandSender sender, String[] args) {
		plugin.loadConfig();
		plugin.loadTiers();
		for (BODPlayer player : plugin.players)
			player.save();
		plugin.players.clear();
		sender.sendMessage(ChatColor.GREEN+"BanOnDeath has been reloaded.");
		
	}

}
