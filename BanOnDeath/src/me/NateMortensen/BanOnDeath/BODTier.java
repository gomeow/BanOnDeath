/**
 * 
 */
package me.NateMortensen.BanOnDeath;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author Nate Mortensen
 * 
 */
public class BODTier {
	int lives;
	long banlength;
	long resettime;
	String name;
	ConfigurationSection section;
	FileConfiguration config;
	BanOnDeath plugin;
	public BODTier(FileConfiguration file, String n, BanOnDeath p){
		config = file;
		plugin = p;
		name = n;
		if (config.contains(name)){
			section = config.getConfigurationSection(name);
		}
		else{
			section = config.createSection(name);
		}
		lives = section.getInt("lives", 1);
		String unit = section.getString("unit", "minute");
		int numberof = section.getInt("numberofunit", 30);
		long resettime = section.getInt("resettime", 7) * 86400000;
		if (unit.equals("minute")) banlength = numberof * 60000;
		else if (unit.equals("hour")) banlength = numberof * 3600000;
		else if (unit.equals("day")) banlength = numberof * 86400000;
		else if (unit.equals("week")) banlength = numberof * 604800000;
		else {
			banlength = numberof * 60000;
			p.log("The unit for tier "+name+" is invalid.  Defaulting to minutes.");
		}
	}
	//Get methods.
	public int getLives()
	{
		return lives;
	}
	public long getBanLength()
	{
		return banlength;
	}
	public long getResetTime()
	{
		return resettime;
	}
	public String getName()
	{
		return name;
	}
	//No set methods.  None of this should be changed.
	
	//Other methods.
	public void save()
	{
		YAPI.saveYaml(plugin, config, "tiers.yml");
	}

}
