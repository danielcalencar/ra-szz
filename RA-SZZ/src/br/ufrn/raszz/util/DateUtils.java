package br.ufrn.raszz.util;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.List;
import java.util.ArrayList;

/**
 * This class should implements all utilities methods about Date manipulation
 * 
 * @author danielcalencar
 * 
 */
public class DateUtils {
	public static final String GMT3 = "Etc/GMT+3";
	private static DateFormatSymbols symbols = new DateFormatSymbols();
	private static String[] newMonths = { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV",
			"DEC" };
	private static String[] newShortMonths = { "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "oct",
			"nov", "dec" };
	private static String[] newWeekdays = { "", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturaday",
			"Sunday" };
	private static String[] shortWeekdays = { "", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun" };

	public static SimpleDateFormat getSimpleDateFormat(String format, String timezone) {
		symbols.setMonths(newMonths);
		symbols.setShortMonths(newShortMonths);
		symbols.setWeekdays(newWeekdays);
		symbols.setShortWeekdays(shortWeekdays);
		SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.CANADA);
		sdf.setDateFormatSymbols(symbols);
		if (timezone != null)
			sdf.setTimeZone(TimeZone.getTimeZone(timezone));
		return sdf;
	}

	public static Date truncTimeFromDate(Date date) {
		Calendar cal = Calendar.getInstance(Locale.CANADA); // locale-specific
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	public static Date incrementDays(Date date, int days) {
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, days);
		return cal.getTime();
	}

	public static Date parseDateWithFormat(String format, String date, String timezone) throws ParseException {
		SimpleDateFormat sdf = getSimpleDateFormat(format, null);
		return sdf.parse(date);
	}

	public static void main(String args[]) throws Exception {
		List<String> dates = new ArrayList<String>();
		for(String arg : args){
			dates.add(arg);
		}
		for(String sdate : dates){
			String dateToBeParsed = sdate;
			SimpleDateFormat sdf = getSimpleDateFormat("EEE MMM dd HH:mm:ss yyyy zzzzz", null);
			SimpleDateFormat sdf2 = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss", null);
			Date date = sdf.parse(dateToBeParsed);
			String mydate = sdf2.format(date);
			System.out.println(mydate);
		}

	}
	//public static void main(String args[]) throws Exception {
	//	String dateToBeParsed = "2002-01-03 16:41:37 EST (History)";
	//	SimpleDateFormat sdf = getSimpleDateFormat("yyyy-MM-dd HH:mm:ss z '[A-Za-z()]' ", null);
	//	Date date = sdf.parse(dateToBeParsed);
	//	System.out.println("aqui");
	//}

}
