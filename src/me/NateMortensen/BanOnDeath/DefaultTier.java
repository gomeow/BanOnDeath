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
public class DefaultTier extends EasyConfigurationSection implements Tier {

	@Load
	int resettime = 7;
	@Load
	int banDelay = 0;
	@Load
	int numberofunit = 30;
	@Load
	int lives = 0;
	@Load
	String unit = "minute";
	@Load
	String message = "You have failed";
	@Load
	String failedRejoinMessage = "Rejoin in : %M minutes";
	//These are calculated values, they shouldn't be loaded.
	long banlength, reset;	


	/**
	 * @param c
	 */
	public DefaultTier(ConfigurationSection c) {
		super(c);
		calculateBanLength();

	}
	protected void calculateBanLength(){
		try {
			long unitValue = TimeUnit.valueOf(unit.toUpperCase()).getTime();
			banlength = unitValue * numberofunit;
			reset = resettime * TimeUnit.DAY.getTime();
		} catch(IllegalArgumentException e){
			throw new InvalidConfigurationException("Invalid unit for tier:"+getName()+".  Please report the problem if you believe this is a bug.");
		}
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getReset()
	 */
	@Override
	public long getReset() {
		return reset;
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getBanLength()
	 */
	@Override
	public long getBanLength() {
		return banlength;
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getBanMessage()
	 */
	@Override
	public String getBanMessage() {
		return message;
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getLives()
	 */
	@Override
	public int getLives() {
		return lives;
	}
	/* (non-Javadoc)
	 * @see me.NateMortensen.BanOnDeath.Tier#getBanDelay()
	 */
	@Override
	public int getBanDelay() {
		return banDelay;
	}
	public String getFailedReconnectMessage() {
		return failedRejoinMessage;
	}


}
