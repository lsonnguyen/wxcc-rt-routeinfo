package com.cisco.wxcc.router.util;

import java.util.Calendar;
import java.util.TimeZone;

public class DatetimeUtil {

	private static String timezone = "US/Eastern";

	public static Long currentTimeEpoch() {
		Calendar c =  Calendar.getInstance(TimeZone.getTimeZone(timezone));
		return c.getTimeInMillis();
	}

	public static Long todayStartEpoch() {
		Calendar c =  Calendar.getInstance(TimeZone.getTimeZone(timezone));
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);

		return c.getTimeInMillis();
	}

	public static boolean secondsElapsed(long epoch, int seconds) {
		return (System.currentTimeMillis() - epoch >= seconds * 1000);
	}
}
