package com.lixy.ftapi.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	protected static final Date MAX_DATE = new Date(Long.MAX_VALUE);
	private static final String DATES_MUST_BE_NOT_NULL = "The dates must be NOT null";
	
	private DateUtils(){
		//make singleton
	}
	
	public static boolean isSameDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isSameDay(cal1, cal2);
	}

	public static boolean isSameDay(Calendar cal1, Calendar cal2) {
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean isToday(Date date) {
		return isSameDay(date, Calendar.getInstance().getTime());
	}

	public static boolean isToday(Calendar cal) {
		return isSameDay(cal, Calendar.getInstance());
	}

	public static boolean isBeforeDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isBeforeDay(cal1, cal2);
	}

	public static boolean isBeforeDay(Calendar cal1, Calendar cal2) { // NOSONAR
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA))
			return true;
		if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA))
			return false;
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
			return true;
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
			return false;
		return cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean isAfterDay(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		return isAfterDay(cal1, cal2);
	}

	public static boolean isAfterDay(Calendar cal1, Calendar cal2) { // NOSONAR
		if (cal1 == null || cal2 == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA))
			return false;
		if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA))
			return true;
		if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR))
			return false;
		if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR))
			return true;
		return cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR);
	}

	public static boolean isWithinDaysFuture(Date date, int days) {
		if (date == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return isWithinDaysFuture(cal, days);
	}

	public static boolean isWithinDaysFuture(Calendar cal, int days) {
		if (cal == null) {
			throw new IllegalArgumentException(DATES_MUST_BE_NOT_NULL);
		}
		Calendar today = Calendar.getInstance();
		Calendar future = Calendar.getInstance();
		future.add(Calendar.DAY_OF_YEAR, days);
		return isAfterDay(cal, today) && !isAfterDay(cal, future);
	}

	public static Date getStart(Date date) {
		return clearTime(date);
	}

	public static Date clearTime(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	public static boolean hasTime(Date date) { // NOSONAR
		if (date == null) {
			return false;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (c.get(Calendar.HOUR_OF_DAY) > 0) {
			return true;
		}
		if (c.get(Calendar.MINUTE) > 0) {
			return true;
		}
		if (c.get(Calendar.SECOND) > 0) {
			return true;
		}
		if (c.get(Calendar.MILLISECOND) > 0) {
			return true;
		}
		return false;
	}

	public static Date getEnd(Date date) {
		if (date == null) {
			return null;
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		return c.getTime();
	}

	public static Date max(Date d1, Date d2) {
		if (d1 == null && d2 == null)
			return null;
		if (d1 == null)
			return d2;
		if (d2 == null)
			return d1;
		return d1.after(d2) ? d1 : d2;
	}

	public static Date min(Date d1, Date d2) {
		if (d1 == null && d2 == null)
			return null;
		if (d1 == null)
			return d2;
		if (d2 == null)
			return d1;
		return d1.before(d2) ? d1 : d2;
	}

	public static String getDateFromMilisecWithFormat(long miliseconds, String format) {
		DateFormat formatter = new SimpleDateFormat(format);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(miliseconds);

		return formatter.format(calendar.getTime());
	}

	public static Date getDateFromMilisec(long miliseconds) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(miliseconds);

		return calendar.getTime();
	}
	
	public static Date substractTime(Date date, int type, int seconds){
		Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
		calendar.setTime(date);
		calendar.add(type, -1*seconds);
		return calendar.getTime();
	}
	
	public static Date addTime(Date date, int type, int seconds){
		Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
		calendar.setTime(date);
		calendar.add(type, seconds);
		return calendar.getTime();
	}


}