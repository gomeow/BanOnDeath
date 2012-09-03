/**
 * 
 */
package me.NateMortensen.BanOnDeath;

/**
 * @author Nate Mortensen
 *
 */
public enum TimeUnit {
	SECOND(1000),
	SECONDS(1000),
	MINUTE(60000),
	MINUTES(60000),
	HOUR(3600000),
	HOURS(3600000),
	DAY(86400000),
	DAYS(86400000),
	WEEK(604800000),
	WEEKS(604800000);
	
	
	long time;
	private TimeUnit(long time){
		this.time = time;
	}
	
	public long getTime(){
		return time;
	}
	
	
	
	

}
