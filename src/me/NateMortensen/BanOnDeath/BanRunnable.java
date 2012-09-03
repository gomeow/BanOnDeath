/**
 * 
 */
package me.NateMortensen.BanOnDeath;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * @author Nate Mortensen
 *
 */
public class BanRunnable implements Runnable{

	BODPlayer p;
	Tier tier;
	public BanRunnable(BODPlayer p, Tier tier){
		this.p = p;
		this.tier = tier;
	}
	@Override
	public void run() {
		Player player = Bukkit.getPlayer(p.getName());
		if (player.isOnline())
			player.kickPlayer(tier.getBanMessage());
		p.ban(tier);
	}

}
