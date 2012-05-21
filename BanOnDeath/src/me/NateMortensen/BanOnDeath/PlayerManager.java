/**
 * 
 */
package me.NateMortensen.BanOnDeath;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

/**
 * @author Nate Mortensen
 * 
 */
public class PlayerManager {
	BanOnDeath plugin;
	public FileConfiguration players;
	FileConfiguration tiers;
	public PlayerManager(BanOnDeath p){
        players = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "players.yml"));
        tiers = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "tiers.yml"));
		plugin = p;
		//Check the config file for a default tier.
        if (!(tiers.contains("default"))){
        	tiers.set("default.unit", "minute");
        	tiers.set("default.amount_of_unit", 30);
        	tiers.set("default.lives", 1);
        	tiers.set("default.resettime", 7);
        	YAPI.saveYaml(plugin, tiers);
        }
	}
    public long getLastBanTime(Player player){
    	return players.getLong(player.getName().toLowerCase() + ".lastbantime");
    }
    public long getLastBanTime(String player){
    	return players.getLong(player.toLowerCase() + ".lastbantime");
    }
    public int getLives(Player player){
    	return players.getInt(player.getName().toLowerCase() + ".lives");
    }
    public int getLives(String player){
    	return players.getInt(player.toLowerCase() + ".lives");
    }
    public void banPlayer(Player player){
    	players.set(player.getName().toLowerCase()+".lastbantime", System.currentTimeMillis());
    	clearInventory(player);
    	player.kickPlayer(plugin.kickmessage);
    }
    public void clearInventory(final Player player){
    	PlayerInventory inventory = player.getInventory();
    	inventory.clear();
    	inventory.setArmorContents(null);
    }
    public long getUnbanDate(final Player player) {
        return getBanLength(getTier(player)) + getLastBanTime(player);
    }
    public void setLives(Player player, int amount) {
    	players.set(player.getName().toLowerCase()+".lives", amount);
    }
    public long getBanLength(String tier) {
        final String unit;
        final long numberof;
        if (tier == null) {
            unit = tiers.getString("default.unit");
            numberof = tiers.getLong("default.amount_of_unit");
        } else {
            unit = tiers.getString(tier + ".unit");
            numberof = tiers.getLong(tier + ".amount_of_unit");
        }
        if (unit.equals("minute")) {
            return numberof * 60000L;
        } else if (unit.equals("second")) {
            return numberof * 1000L;
        } else if (unit.equals("hour")) {
            return numberof * 3600000L;
        } else if (unit.equals("day")) {
            return numberof * 86400000L;
        } else {
            final Logger logger = plugin.getLogger();
            logger.info(tier + " has an error in its units, using the default value instead.");
            if (tier.equals("default")) {
                logger.info("[BanOnDeath][SEVERE]  Default tier's unit is not valid!");
                return 0;
            } else {
                return getBanLength("default");
            }
        }
    }

    public String getTier(final Player player) {
    	String[] thetiers = new String[tiers.getKeys(false).size()];
		for (int x = 0; x < tiers.getKeys(false).size(); x++){
			thetiers[x] = tiers.getKeys(false).iterator().next();
			tiers.getKeys(false).remove(tiers.getKeys(false).iterator().next());
		}
        for (int i = 0; i < thetiers.length; i++) {
            if (player.hasPermission("bod.tiers."+ thetiers[i])) {
                return thetiers[i];
            }
        }
        return "default";
    }

    public void resetLives(final Player player) {
        final int targetLives = tiers.getInt(getTier(player) + ".lives");
        if (targetLives == 0) {
            return;
        }
        final String playerName = player.getName().toLowerCase();
        players.set(playerName + ".lives", targetLives);
        players.set(playerName + ".lastreset", System.currentTimeMillis());
    }
    public Boolean isOnline(String name){
    	final Player[] onlineplayers = plugin.getServer().getOnlinePlayers();
    	for (int i = 0; i < onlineplayers.length; i++){
    		if (onlineplayers[i].getName() == name){
    			return true;
    		}
    	}
    	return false;
    }


    public long getPlayerConfigLong(Player player, final String node) {
        return players.getLong(player.getName().toLowerCase() + "." + node);
    }

    public boolean hasPlayerConfig(Player player, final String node) {
        return players.contains(player.getName().toLowerCase() + "." + node);
    }

    public Boolean isBanned(Player player) {
        if (!hasPlayerConfig(player, "lastbantime")) {
            return false;
        }

        if (System.currentTimeMillis() - getPlayerConfigLong(player, "lastbantime") < getBanLength(getTier(player))) {
            return true;
        }
        return false;
    }

    public boolean needsReset(final Player player) {
        if (!hasPlayerConfig(player, "lastreset")) {
            return false;
        }
        if ((System.currentTimeMillis() - getPlayerConfigLong(player, "lastreset") < tiers.getInt(getTier(player) + ".resettime"))) {
            return false;
        }
        return true;
    }
    public void savePlayers()
    {
    	YAPI.saveYaml(plugin, players);
    }


}
