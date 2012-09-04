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
	public int resettime = 7;
	@Load
	public int banDelay = 1;
	@Load
	public int numberofunit = 30;
	@Load
	public int lives = 0;
	@Load
	public String unit = "minute";
	@Load
	public String message = "You have failed";
	@Load
	public String failedRejoinMessage = "Rejoin in : %M minutes";
	@Load
	public String deathAnnouncement = "%P has been banned for %M minutes.";
	@Load
	public boolean announceDeath = true;
	@Load
	public boolean resetExtraLives = false;
	//These are calculated values, they shouldn't be loaded.
	long banlength, reset;	


	/**
	 * @param c
	 */
	public DefaultTier(ConfigurationSection c) {
		super(c);
		load();
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
	public String getBanAnnouncement(){
		return deathAnnouncement;
	}
	public boolean getAnnounceBan(){
		return announceDeath;
	}
	public boolean getResetExtraLives(){
		return resetExtraLives;
	}


}
