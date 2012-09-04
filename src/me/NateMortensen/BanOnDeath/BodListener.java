package me.NateMortensen.BanOnDeath;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * ***
 *
 * @author Nate Mortensen
 */
public class BodListener implements Listener {

	private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
	private final BanOnDeath plugin = BanOnDeath.getInstance();

	public BodListener() {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		if (event instanceof PlayerDeathEvent) {
			Player theplayer = (Player)event.getEntity();
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
				plugin.getServer().dispatchCommand((CommandSender)theplayer, "lives");
				return;
			}
			final long now = System.currentTimeMillis();
			// Player ban code goes below.
			BanRunnable runnable = new BanRunnable(player, tier);
			if (tier.getBanDelay() <= 0)
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable);
			else
				plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, runnable, tier.getBanDelay()*20);
			if (tier.getAnnounceBan())
				((PlayerDeathEvent)event).setDeathMessage(player.getDeathAnnouncement(tier));
			if (plugin.logToFile) {
				final Date nowDate = new Date(now);
				final Date unbanDate = new Date(player.getUnbanDate());
				try {
					PrintWriter pw = new PrintWriter(new FileWriter(plugin.file.getPath(), true));
					pw.println(player.getName() + ", "
							+ dateFormatter.format(nowDate) + ", "
									+ dateFormatter.format(unbanDate) + ", "
									+ ((PlayerDeathEvent) event).getDeathMessage());
					pw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//    @EventHandler
	//    public void onEntitySpawn(final PlayerRespawnEvent event) {
	//        final String playerName = event.getPlayer().getName();
	//        if (playersPendingList.contains(playerName)) {
	//            playersPendingList.remove(playerName);
	//
	//            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
	//
	//                public void run() {
	//                    for (String e : plugin.getConfig().getStringList("Command-To-Run-On-Death")) {
	//                        if (e.contains("{name}")) {
	//                            e = e.replaceAll("\\{name\\}", playerName);
	//                        }
	//                        System.out.println("Now executing " + e);
	//                        plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), e);
	//                    }
	//                }
	//            }, 1L);
	//        }
	//    }


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
		if (plugin.players.contains(player)){
			player.save();
			plugin.players.remove(player);
		}
	}
}
