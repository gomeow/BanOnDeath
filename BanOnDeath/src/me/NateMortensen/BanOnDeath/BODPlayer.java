/**
 * 
 */
package me.NateMortensen.BanOnDeath;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Nate Mortensen
 * The idea of this class is not that it represent an online player, but rather represent the config data of a player.
 * The difficult part of this is that You can't get the permissions of an offline player, and thus you can't just reset them.
 * For that I had to add in the needsreset so that they can have their lives reset when they're next online.
 */
public class BODPlayer {
	long banlength, bantime, lastreset;
	String name;
	boolean needsreset;
	int lives;	
	BanOnDeath plugin;
	ConfigurationSection section;
	FileConfiguration file;
	
	
	public BODPlayer(FileConfiguration config, String n, BanOnDeath p){
		
		file = config;
		name = n.toLowerCase();
		plugin = p;
		
		if (config.contains(name)){
			section = config.getConfigurationSection(name);
		}
		else{
			section = config.createSection(name);
		}
		
		//Load everything from the config, or get the default values.
		banlength = section.getLong("banlength", 1800000);
		bantime = section.getLong("bantime", 0);
		lastreset = section.getLong("lastreset", 0);
		needsreset = section.getBoolean("needsreset", true);
		lives = section.getInt("lives", 1);
		

	}
	//Accessors.  
	public long getBanLength(){
		return banlength;
	}
	public long getBanTime(){
		return bantime;
	}
	public String getName(){
		return name;
	}
	public boolean getNeedsReset(){
		return needsreset;
	}
	public int getLives(){
		return lives;
	}
	public long getLastReset(){
		return lastreset;
	}
	public long getUnbanDate(){
		return bantime + banlength;
	}
	
	//Mutators.  No setName method as that shouldn't be changeable.
	public void setBanLength(long newval){
		banlength = newval;
		save();
	}
	public void setBanTime(long newval){
		bantime = newval;
		save();
	}
	public void setLives(int newval){
		lives = newval;
		save();
	}
	public void setNeedsReset(boolean newval){
		needsreset = newval;
		save();
	}
	public void setLastReset(long newval){
		lastreset = newval;
		save();
	}
	
	//other methods.
	public Boolean isBanned(){
		if (System.currentTimeMillis() - bantime < banlength) return true;
		return false;
	}
	public void reset(BODTier tier){
		needsreset = false;
		lives = tier.getLives();
		lastreset = System.currentTimeMillis();
		bantime = 0;
		save();
		
	}
	public Boolean needsReset(long resettime){
		if (needsreset) return true;
		if (System.currentTimeMillis() - lastreset > resettime) return true;
		return false;
	}
	public void save(){
		section.set("banlength", banlength);
		section.set("bantime", bantime);
		section.set("lastreset", lastreset);
		section.set("needsreset", needsreset);
		section.set("lives", lives);
		file.set(section.getCurrentPath(), section);
		YAPI.saveYaml((JavaPlugin)plugin, file, "players.yml");
	}
	public void decreaseLives(int amount){
		lives -= amount;
	}
	public void increaseLives(int amount){
		lives += amount;
	}
	public void ban(BODTier tier){
		bantime = System.currentTimeMillis();
		banlength = tier.getBanLength();
	}

}
