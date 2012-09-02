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
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * ***
 *
 * @author Nate Mortensen
 */
public class BodListener implements Listener {

    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private final BanOnDeath plugin;

    public BodListener(final BanOnDeath plugin) {
        this.plugin = plugin;
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
            BODTier tier = plugin.getTierOfPlayer(theplayer);
            if (player.needsReset(tier.getResetTime())){
            	player.reset(tier);
            	theplayer.sendMessage("You've been saved!  Your lives have been reset!");
            }
            //Lives check
            if (player.getLives() > 0){
            	player.decreaseLives(1);
            	plugin.getServer().dispatchCommand((CommandSender)theplayer, "lives");
            	return;
            }
            theplayer.getInventory().clear();
            theplayer.getInventory().setArmorContents(null);
            final long now = System.currentTimeMillis();
            // Player ban code goes below.
            player.ban(tier);
            theplayer.kickPlayer(plugin.kickmessage);
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


    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
    	BODPlayer player = plugin.getPlayer(event.getPlayer().getName());
        if (player.isBanned()) {
            Date date = new Date(player.getUnbanDate());
            final String kickMsg = "Rejoin on: " + dateFormatter.format(date);
            event.disallow(Result.KICK_OTHER, kickMsg);
            return;
        }
        BODTier tier = plugin.getTier(player.getName().toLowerCase());
        if (player.needsReset(tier.getBanLength())){
        	player.reset(tier);
        }
        plugin.loadPlayerToList(event.getPlayer().getName());
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
