/**
 * 
 */
package me.NateMortensen.BanOnDeath;

import org.bukkit.configuration.ConfigurationSection;

import com.bukkitarena.configuration.EasyConfigurationSection;
import com.bukkitarena.configuration.InvalidConfigurationException;
import com.bukkitarena.configuration.Load;

/**
 * @author Nate Mortensen
 * 
 */
public class BODTier extends EasyConfigurationSection implements Tier{
	@Load
	int resettime;
	@Load
	int banDelay;
	@Load
	int numberofunit;
	@Load
	int lives;
	@Load
	String unit;
	@Load
	String message;
	@Load
	String inherit;
	@Load
	String failedRejoinMessage;
	//These are calculated values, they shouldn't be loaded.
	long banlength, reset;	

	public BODTier(ConfigurationSection c){
		super(c);
		calculateBanLength();
	}
	protected void calculateBanLength(){
		try {
			long unitValue = TimeUnit.valueOf(unit.toUpperCase()).getTime();
			banlength = unitValue * numberofunit;
		} catch(IllegalArgumentException e){
			throw new InvalidConfigurationException("Invalid unit for tier:"+getName()+".  Please report the problem if you believe this is a bug.");
		}
	}
	public void inherit(){
		if (inherit != null){
			Tier tier = BanOnDeath.getInstance().getTier(inherit);
			if (tier == null)
				throw new InvalidConfigurationException(getName()+ " was unable to inherit from "+inherit+" as it was unable to find the specified tier.  Verify that it is a valid tier.");
			this.inheritFrom(tier);
		}
	}




	/**
	 * @return the lives
	 */
	public int getLives() {
		return lives;
	}




	/**
	 * @return the numberofunit
	 */
	public int getNumberOfUnit() {
		return numberofunit;
	}




	/**
	 * @return the resettime as defined in the config.
	 */
	public int getResetTime() {
		return resettime;
	}




	/**
	 * @return the unit as defined in the config.
	 */
	public String getUnit() {
		return unit;
	}




	/**
	 * @return the banlength as calculated from the values in the config.
	 */
	public long getBanLength() {
		return banlength;
	}




	/**
	 * @return the reset time as calculated by multiple the value resettime by one day.
	 */
	public long getReset() {
		return reset;
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getBanMessage()
	 */
	@Override
	public String getBanMessage() {
		return message;
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getBanDelay()
	 */
	@Override
	public int getBanDelay() {
		return banDelay;
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getFailedReconnectMessage()
	 */
	@Override
	public String getFailedReconnectMessage() {
		return failedRejoinMessage;
	}



}
