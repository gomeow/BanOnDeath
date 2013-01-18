package me.NateMortensen.BanOnDeath;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * ***
 *
 * @author Nate Mortensen
 */
public class BodListener implements Listener {

	private String capitalizeFirstLetter(String s) {
		String firstLetter = s.substring(0, 1).toUpperCase();
		String rest = s.substring(1);
		return firstLetter+rest;
	}
	
	private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private final BanOnDeath plugin = BanOnDeath.getInstance();

	public BodListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	@EventHandler
	public void onEntityDeath(PlayerDeathEvent event) {
		String killerType = new String();
		Player theplayer = event.getEntity();
		killerType = (theplayer.getKiller() == null)?"Natural":capitalizeFirstLetter(theplayer.getKiller().getType().toString());
		if (theplayer.hasPermission("bod.noban") || theplayer.isOp()) {
			return;
		}
		final BODPlayer player = plugin.getPlayer(theplayer.getName().toLowerCase());
		//Check if the player needs a life reset, and if so, reset their lives.
		Tier tier = plugin.getTierOfPlayer(theplayer);
		if (player.needsReset(tier)){
			player.reset(tier);
			theplayer.sendMessage("You've been saved!  Your lives have been reset!");
		}
		//Lives check
		if (player.getLives() > 0){
			player.decreaseLives(1);
			plugin.getServer().dispatchCommand(theplayer, "lives");
			return;
		}
		final long now = System.currentTimeMillis();
		// Player ban code goes below.
		BanRunnable runnable = new BanRunnable(player, tier);
		event.setDeathMessage(null);
		if (tier.getBanDelay() <= 0)
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable);
		else
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, tier.getBanDelay()*20);
		if (plugin.logToFile) {
			final Date nowDate = new Date(now);
			final Date unbanDate = new Date(player.getUnbanDate());
			try {
				PrintWriter pw = new PrintWriter(new FileWriter(plugin.file.getPath(), true));
				pw.println(player.getName() + ", "
						+ dateFormatter.format(nowDate) + ", "
								+ dateFormatter.format(unbanDate) + ", "
								+ event.getDeathMessage());
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		player.save();
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerJoinEvent event) {
		BODPlayer player = plugin.getPlayer(event.getPlayer().getName());
		Tier tier = plugin.getTier(player.getName().toLowerCase());
		if (player.needsReset(tier))
			player.reset(tier);
		if (player.isBanned()) {
			final String kickMsg = player.getFailedRejoinMessage(tier);
			event.getPlayer().kickPlayer(kickMsg);
			event.setJoinMessage(null);
			return;
		}
		plugin.players.add(player);
	}
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event){
		BODPlayer player = plugin.getPlayer(event.getPlayer().getName());
		if (player.isBanned())
			event.setQuitMessage(null);
		if (plugin.players.contains(player)){
			player.save();
			plugin.players.remove(player);
		}
	}
}
