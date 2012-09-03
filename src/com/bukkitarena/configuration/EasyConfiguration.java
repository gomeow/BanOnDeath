/**
 * 
 */
package com.bukkitarena.configuration;

import java.io.File;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 * @author Nate Mortensen
 * 
 * 
 * 
 */
public abstract class EasyConfiguration extends EasyConfigurationSection{
	public EasyConfiguration(File f){
		super((ConfigurationSection)YamlConfiguration.loadConfiguration((f)));
	}
	/**This is an alternate constructor, meant mostly
	 * 
	 * @param c The FileConfiguraiton to be wrapped by this class.
	 */
	public EasyConfiguration(FileConfiguration c){
		super((ConfigurationSection) c);
	}
	

	

}
