package me.NateMortensen.BanOnDeath;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

/**
 * ***
 *
 * @author Nate Mortensen
 */
public class BodListener implements Listener {

    private static final DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final BanOnDeath plugin;
    private final PlayerManager playermanager;

    public BodListener(final BanOnDeath plugin) {
        this.plugin = plugin;
        playermanager=plugin.playermanager;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event instanceof PlayerDeathEvent) {
            final Player player = (Player) event.getEntity();
            if (player.hasPermission("bod.noban") || player.isOp()) {
                return;
            }
            int playerLives = playermanager.getLives(player);
            //Lives check
            if (playerLives > 0) {
            	playerLives -= 1;
                playermanager.setLives(player, playerLives);
                player.sendMessage("You have " + playerLives + " lives remaining.");
                return;
            }
            final long now = System.currentTimeMillis();
            // Player ban code goes below.
            playermanager.banPlayer(player);

            if (plugin.logToFile == false) {
                final Date nowDate = new Date(now);
                final Date unbanDate = new Date(now + playermanager.getUnbanDate(player));
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
        if (playermanager.isBanned(event.getPlayer())) {
            Date date = new Date(plugin.playermanager.getBanLength(playermanager.getTier(event.getPlayer())) + playermanager.getLastBanTime(event.getPlayer()));
            final String kickMsg = "Rejoin on: " + dateFormatter.format(date);
            event.setKickMessage(kickMsg); //Workaround for esoteric bug
            event.disallow(Result.KICK_BANNED, kickMsg);
            return;
        }
        final Player player = event.getPlayer();

        if (!(playermanager.hasPlayerConfig(player, ".lives"))) {
            playermanager.resetLives(player);
            return;
        }

        
        if (playermanager.needsReset(player)) {
            playermanager.resetLives(player);
        }
    }
}
