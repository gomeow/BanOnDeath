/**
 * 
 */
package me.NateMortensen.BanOnDeath;

/**
 * @author Nate Mortensen
 *
 */
public interface Tier {
	
	public long getReset();
	public long getBanLength();
	public String getBanMessage();
	public String getFailedReconnectMessage();
	public String getName();
	public boolean getAnnounceDeath();
	public String getDeathAnnouncement();
	public boolean getResetExtraLives();
	public int getLives();
	public int getBanDelay();

}
