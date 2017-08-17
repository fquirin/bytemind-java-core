package de.bytemind.core.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Tools to extract dates and times and to create them.
 * 
 * @author Florian Quirin
 *
 */
public class DateTime {
	
	public static final long DAY_MS = (1000l * 60l * 60l * 24l);
	public static final long HOUR_MS = (1000l * 60l * 60l);
	public static final long MINUTE_MS = (1000l * 60l);
	public static final long SECOND_MS = (1000l);
	
	public static final String ISODateFormat = "yyyy-MM-dd'T'HH:mm'Z'";
	
	/**
	 * Get default date string for logger. 
	 */
	public static String getLogDate(){
		return getFormattedDate("yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * Get locale date string with custom format. 
	 */
	public static String getFormattedDate(String format){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(date);
	}
	/**
	 * Get a custom formatted string with the current GMT Time/Date.
	 * @param format - desired format like "yyyy/dd/MM" or "dd.MM.yyyy' - 'HH:mm:ss' - GMT'"
	 * @return String in the given format
	 */
	public static String getGMT(String format){
		Date date = new Date();
		return getGMT(date, format);
	}
	/**
	 * Get a custom formatted string with the GMT Time/Date of the given "date".
	 * @param date - Date object allocated at some point of time
	 * @param format - desired format like "HH:mm:ss" or "dd.MM.yyyy"
	 * @return String in given format at given date
	 */
	public static String getGMT(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		return sdf.format(date);
	}
	/**
	 * Get a custom formatted string with the current UTC Time/Date.
	 * @param format - desired format like "yyyy/dd/MM" or "dd.MM.yyyy' - 'HH:mm:ss' - GMT'"
	 * @return String in the given format
	 */
	public static String getUTC(String format){
		Date date = new Date();
		return getGMT(date, format);
	}
	/**
	 * Get a custom formatted string with the UTC Time/Date of the given "date".
	 * @param date - Date object allocated at some point of time
	 * @param format - desired format like "HH:mm:ss" or "dd.MM.yyyy"
	 * @return String in given format at given date
	 */
	public static String getUTC(Date date, String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(date);
	}
	/**
	 * Get a custom formatted string of the given UNIX time at a given timezone.
	 * @param unixTS - time-stamp of UNIX time
	 * @param format - desired format like "HH:mm:ss" or "dd.MM.yyyy"
	 * @param timeZone - String representation of time zone like "America/Los_Angeles"
	 */
	public static String getDateAtTimeZone(long unixTS, String format, String timeZone){
		Date date = new Date(unixTS);
		TimeZone tz = TimeZone.getTimeZone(timeZone);
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setTimeZone(tz);
		return sdf.format(date);
	}
	
	/**
	 * Convert a date string from one format to another. Make sure the input is actually given in the expected format or the
	 * result will be empty!
	 * @param date_string - string with date in expected format
	 * @param format_in - expected input format, e.g. "dd.MM.yyyy"
	 * @param format_out - desired output format
	 * @return date string in desired format or empty
	 */
	public static String convertDateFormat(String date_string, String format_in, String format_out){
		//parse input
		SimpleDateFormat sdf_in = new SimpleDateFormat(format_in);
		Date date;
		try {
			date = sdf_in.parse(date_string);
		} catch (ParseException e) {
			Debugger.println("DateTime.convertDateFormat() - failed to parse: " + date_string, 1);
			return "";
		}
		//make new one
		SimpleDateFormat sdf_out = new SimpleDateFormat(format_out);
		return sdf_out.format(date);
	}
	
	/**
	 * Get the local calendar of the user to determine things like dayOfWeek etc..
	 * Returns null if userTimeLocal is null, empty or in wrong format.
	 * @param userTimeLocal - String in ISO format like 2017-07-24T21:54:31.962Z
	 */
	public static Calendar getUserCalendar(String userTimeLocal){
		if (userTimeLocal != null && !userTimeLocal.isEmpty()){
			//parse local date
			SimpleDateFormat def_sdf = new SimpleDateFormat(ISODateFormat);
			Date date;
			try {
				date = def_sdf.parse(userTimeLocal);
			} catch (ParseException e) {
				Debugger.println("DateTime.getUserCalendar() - failed to parse: " + userTimeLocal, 1);
				return null;
			}
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			return c;
		}else{
			return null;
		}
	}
	
	/**
	 * Get today by parsing user local time string (in ISO format)
	 * @param format - e.g.: "dd.MM.yyyy" or "HH:mm:ss" or "MM/dd/yy"
	 * @param user_time_local - String in ISO format like 2017-07-24T21:54:31.962Z
	 * @return todays date at user location as string or empty string (if client does not submit it)
	 */
	public static String getToday(String format, String user_time_local){
		if (user_time_local != null && !user_time_local.isEmpty()){
			//parse local date
			SimpleDateFormat def_sdf = new SimpleDateFormat(ISODateFormat);
			Date date;
			try {
				date = def_sdf.parse(user_time_local);
			} catch (ParseException e) {
				return "";
			}
			//make new one
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			String today = sdf.format(date);
			return today;
		}else{
			return "";
		}
	}

}
