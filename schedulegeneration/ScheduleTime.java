package schedulegeneration;

import java.util.ArrayList;

/**
 * <p>
 * A time object containing {@link hour} and {@link minute} static fields in the
 * 24-hour time format.
 *
 * This time object accepts many different types of input times,
 * in 12- or 24-hour formats. It also provides methods to round times to desired
 * precisions or minute values for various applications.
 * </p>
 * <p>
 * ScheduleTime also aids in reformatting strings to aid with outputting
 * the object's values to a user.
 * </p>
 */
public class ScheduleTime {
	/**
	 * Hour value (in 24-hour time)
	 */
	public int hour;

	/**
	 * Minute value
	 */
	public int minute;

	/**
	 * Constructs a new ScheduleTime instance set at the time in the given
	 * string.
	 *
	 * The parameter String time is assumed to be in 12- or 24-hour time.
	 *
	 * @param time	the String containing the desired time
	 */
	public ScheduleTime(String time) {
		if(time.indexOf("am") != -1 || time.indexOf("pm") != -1) {
			time = convert12To24Hour(time);
		}
		this.hour = Integer.parseInt(time.substring(0, time.indexOf(':')));
		this.minute = Integer.parseInt(time.substring(time.indexOf(':') + 1));
	}
	
	/**
	 * Constructs a new ScheduleTime instance set to 00:00 (midnight).
	 */
	public ScheduleTime() {
		this("00:00");
	}

	/**
	 * Constructs a new ScheduleTime instance with the given hour and minute
	 * values.
	 *
	 * The input hour value is assumed to be in 24-hour format.
	 *
	 * @param hour		the hour value desired
	 * @param minute	the minute value desired
	 */
	public ScheduleTime(int hour, int minute) {
		this.hour   = hour;
		this.minute = minute;
	}

	/**
	 * Returns time value in minutes from 00:00 (midnight)
	 *
	 * @return	the total value in minutes
	 */
	public int getMinuteValue() {
		return this.hour * 60 + this.minute;
	}
	
	/**
	 * Rounds the ScheduleTime instance's time forward to the next given minuteVal
	 *
	 * @param minuteVal	the desired minute value
	 */
	public void roundUp(int minuteVal) {
		if(this.minute > minuteVal) {
			this.hour += 1;
		}
		this.minute = minuteVal;
	}
	
	/**
	 * Rounds the ScheduleTime instance's time forward to the next hour
	 */
	public void roundUp() {
		this.roundUp(0);
	}
	
	/**
	 * Rounds the ScheduleTime instance's time backward to the previous given minuteVal
	 *
	 * @param minuteVal	the desired minute value
	 */
	public void roundDown(int minuteVal) {
		if(this.minute < minuteVal) {
			this.hour -= 1;
		}
		this.minute = minuteVal;
	}
	
	/**
	 * Rounds the ScheduleTime instance's time backward to the previous hour
	 */
	public void roundDown() {
		this.roundDown(0);
	}

	/**
	 * <p>
	 * Rounds the ScheduleTime instance's time to a given minute precision.
	 *
	 * The precision is assumed be a clean division of an hour; uneven intervals
	 * start cleanly on the hour and reset to each hour, which could result in
	 * unexpected behavior.
	 * </p>
	 * <p>
	 * The rounding occurs toward the <i>nearest</i> minute interval
	 * value, which may be forward, backward, or no movement at all.
	 * </p>
	 *
	 * @param precisionMinutes	the minute interval precision desired
	 */
	public void roundToPrecision(int precisionMinutes) {
		ArrayList<Integer> distances = new ArrayList<Integer>();
		for(int minutes = 0; minutes <= 60; minutes += precisionMinutes) {
			distances.add(minutes - this.minute);
		}
		int i = 0;
		for(i = 0; i < distances.size() - 1; ++i) {
			if(Math.abs(distances.get(i + 1)) < Math.abs(distances.get(i))) {
				continue;
			}
			break;
		}
		if(distances.get(i) > 0) {
			this.roundUp(precisionMinutes * i);
		}
		else if(distances.get(i) < 0) {
			this.roundDown(precisionMinutes * i);
		}
	}

	/**
	 * Compares two ScheduleTime instances' time values
	 *
	 * The function returns the difference between the two times in minutes,
	 * positive if the first time is later, negative if sooner, and equal
	 * if neither.
	 *
	 * @param a		the ScheduleTime to compare against
	 * @param b		the ScheduleTime to compare to a
	 * @return		the difference between the two times in minutes
	 */
	static public int compareTimes(ScheduleTime a, ScheduleTime b) {
		return a.getMinuteValue() - b.getMinuteValue();
	}
	
	/**
	 * <p>
	 * Reformats a time string from 12-hour time to 24-hour time.
	 *
	 * The input time String is assumed to be in 12 hour time and stored in the
	 * following format:
	 * </p>
	 * <p>
	 * "H:M [am/pm]"
	 * </p>
	 * <p>
	 * where H and M can be any length of numeral characters, but are assumed to
	 * represent values between 1 and 12 (inclusive).
	 * </p>
	 *
	 * @param time		the String int 12-hour time to reformat
	 * @return			the String reformatted to 24-hour time
	 */
	static public String convert12To24Hour(String time) {
		if(time.indexOf(':') != -1 && time.indexOf(' ') != -1) {
			time = time.trim();
			int hour = Integer.parseInt(time.substring(0, time.indexOf(':')).trim());
			int min  = Integer.parseInt(time.substring(time.indexOf(':') + 1, time.indexOf(' ')).trim());
			if(hour < 12 && time.indexOf("pm") != -1) {
				hour += 12;
			}
			else if(hour == 12 && time.indexOf("am") != -1) {
				hour -= 12;
			}
			String newTime = String.format("%02d:%02d", hour, min);
			return newTime;
		}
		return time;
	}
	
	/**
	 * <p>
	 * Reformats a time string from 24-hour time to 12-hour time.
	 *
	 * The input time String is assumed to be in 24 hour time and stored in the
	 * following format:
	 * </p>
	 * <p>
	 * "H:M"
	 * </p>
	 * <p>
	 * where H and M can be any length of numeral characters, but are assumed to
	 * represent values between 0 and 23 (inclusive).
	 * </p>
	 *
	 * @param time		the String int 24-hour time to reformat
	 * @return			the String reformatted to 12-hour time
	 */
	static public String convert24To12Hour(String time) {
		if(time.indexOf(':') != -1) {
			time = time.trim();
			int hour = Integer.parseInt(time.substring(0, time.indexOf(':')).trim());
			int min  = Integer.parseInt(time.substring(time.indexOf(':') + 1).trim());
			String newTime;
			if(hour > 12) {
				newTime = String.format("%02d:%02d pm", hour - 12, min);
			}
			else if(hour == 12) {
				newTime = String.format("%02d:%02d pm", hour, min);
			}
			else if(hour == 0) {
				newTime = String.format("%02d:%02d am", hour + 12, min);
			}
			else {
				newTime = String.format("%02d:%02d am", hour, min);
			}
			return newTime;
		}
		return time;
	}
}