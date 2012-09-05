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
	MINUTE(60000),
	HOUR(3600000),
	DAY(86400000),
	WEEK(604800000);


	long time;
	private TimeUnit(long time){
		this.time = time;
	}
	//No overrides allowed with enumerators...
	public static TimeUnit getEnum(String val){
		for (TimeUnit unit : values())
			if (unit.name().equalsIgnoreCase(val) || (unit.name()+"s").equalsIgnoreCase(val))
				return unit;
		throw new IllegalArgumentException();

	}

	public long getTime(){
		return time;
	}
	public static long translateToTime(long value, TimeUnit unit){
		long origval = value;
		value /= unit.getTime();
		double fractionPart = ( ((double) origval ) / unit.getTime() ) - value;
		if (fractionPart >= 0.5)
			++value;
		return value;
	}
	public static String translateTimeValues(String msg, long value){
		for (TimeUnit unit : values())
			msg = msg.replaceAll("%"+unit.name().substring(0, 1), Long.toString(translateToTime(value, unit)));
		return msg;
	}

}
