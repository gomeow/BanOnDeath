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
	long banlength, resettime;
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
		resettime = section.getInt("resettime", 7) * 86400000;
		
		if (unit.equalsIgnoreCase("minute")) banlength = numberof * 60000;
			else if (unit.equalsIgnoreCase("hour")) banlength = numberof * 3600000;
				else if (unit.equalsIgnoreCase("day")) banlength = numberof * 86400000;
			else if (unit.equalsIgnoreCase("week")) banlength = numberof * 604800000;
		else {
			banlength = numberof * 60000;
			p.log("The unit for tier "+name+" is invalid.  Defaulting to minutes.");
		}
	}
	
	//accessors.
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
	//No mutators.  None of this should be changed.
	
	//Other methods.
	public void save()
	{
		YAPI.saveYaml(plugin, config, "tiers.yml");
	}

}
