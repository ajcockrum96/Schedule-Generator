package schedulegeneration;

import java.util.ArrayList;

/**
 * <p>
 * SGTimeRange is an growth of SGTime, containing
 * a time range and the days on which the time range is applicable.
 *
 * This class contains two SGTime instances that represent the beginning
 * and end of a time range. It also contains the static field of all 7 weekdays
 * for the package {@link schedulegeneration}.
 * </p>
 * <p>
 * Many of the methods within this class are similar to those of SGTime,
 * with added methods to compensate for the properties of a time range.
 * </p>
 * @see SGTime
 */
public class SGTimeRange {
	/**
	 * Weekday string representation assumed for all in {@link schedulegeneration}
	 * package.
	 *
	 * Sunday(S) through Saturday(A)
	 */
	protected static final String weekdays = "SMTWRFA";

	/**
	 * Days on which the SGTimeRange is present/valid/important
	 */
	protected boolean daysUsed[] = {false, false, false, false, false, false, false};

	/**
	 * Beginning SGTime value
	 */
	public SGTime start;

	/**
	 * Ending SGTime value
	 */
	public SGTime end;
	
	/**
	 * Constructs a SGTimeRange with start and end SGTimes as
	 * represented in the time range string on the given days.
	 *
	 * @param rangeString	the String containing the formatted range
	 * @param days			the String containing the used days
	 */
	public SGTimeRange(String rangeString, String days) {
		if(rangeString.indexOf("am") != -1 || rangeString.indexOf("pm") != -1) {
			rangeString = convert12To24HourRange(rangeString);
		}
		this.start = new SGTime(rangeString.substring(0, rangeString.indexOf('-')).trim());
		this.end   = new SGTime(rangeString.substring(rangeString.indexOf('-') + 1).trim());
		for(int i = 0; i < weekdays.length(); ++i) {
			if(days.indexOf(weekdays.charAt(i)) != -1) {
				this.daysUsed[i] = true;
			}
		}
	}

	/**
	 * Constructs a SGTimeRange with start and end SGTimes on the
	 * given days.
	 *
	 * @param start			the SGTime containing the start time
	 * @param end			the SGTime containing the end time
	 * @param days			the String containing the used days
	 */
	public SGTimeRange(SGTime start, SGTime end, String days) {
		this(String.format("%d:%d - %d:%d", start.hour, start.minute, end.hour, end.minute), days);
	}

	/**
	 * Constructs a SGTimeRange with start and end SGTimes on all
	 * of the days in weekdays.
	 *
	 * @param start			the SGTime containing the start time
	 * @param end			the SGTime containing the end time
	 */
	public SGTimeRange(SGTime start, SGTime end) {
		this(String.format("%d:%d - %d:%d", start.hour, start.minute, end.hour, end.minute), weekdays);
	}

	/**
	 * Constructs a duplicate SGTimeRange.
	 *
	 * @param timeRange		the SGTimeRange to duplicate
	 */
	public SGTimeRange(SGTimeRange timeRange) {
		this(timeRange.rangeString(), timeRange.getDays());
	}
	
	/**
	 * Constructs a SGTimeRange with start and end SGTimes as
	 * represented in the time range string on all of the days in weekdays.
	 *
	 * @param rangeString	the String containing the formatted range
	 */
	public SGTimeRange(String rangeString) {
		this(rangeString, weekdays);
	}
	
	/**
	 * Constructs a SGTimeRange of a full day on all of the days in weekdays.
	 */
	public SGTimeRange() {
		this("00:00 - 23:59", weekdays);
	}
	
	/**
	 * Returns a String representing the SGTimeRange in 24-hour format.
	 *
	 * @return		the String of the formatted range
	 */
	public String rangeString() {
		return String.format("%02d:%02d - %02d:%02d", this.start.hour, this.start.minute, this.end.hour, this.end.minute);
	}
	
	/**
	 * Returns the length of the SGTimeRange in decimal hours.
	 *
	 * @return		the decimal length of the range in hours
	 */
	public double getHourLength() {
		return ((this.end.hour - this.start.hour) + (double)((this.end.minute) - (this.start.minute)) / 60.0);
	}
	
	/**
	 * Returns the length of the SGTimeRange in minutes.
	 *
	 * @return		the length of the range in minutes
	 */
	public int getMinuteLength() {
		return ((this.end.hour - this.start.hour) * 60 + (this.end.minute - this.start.minute));
	}

	/**
	 * Returns a String containing the days represented by the SGTimeRange
	 * in the same order as {@link weekdays}.
	 *
	 * @return		the String of days used
	 */
	public String getDays() {
		String days = "";
		for(int i = 0; i < weekdays.length(); ++i) {
			if(this.daysUsed[i]) {
				days = days.concat(weekdays.substring(i, i + 1));
			}
		}
		return days;
	}
	
	/**
	 * <p>
	 * Reformats a time range String from 12-hour time to 24-hour time format.
	 *
	 * The input time range String is assumed to be in a 12 hour format and stored in the
	 * following format:
	 * </p>
	 * <p>
	 * "H:M[am/pm]-H:M[am/pm]"
	 * </p>
	 * <p>
	 * where H and M can be any length of numeral characters, but are assumed to
	 * represent values between 1 and 12 (inclusive).  The string <i>can</i> have
	 * spaces between any of its members and still be read properly.
	 * </p>
	 *
	 * @param timeRange		the String int 12-hour time range to reformat
	 * @return				the String reformatted to a 24-hour time range
	 */
	static public String convert12To24HourRange(String timeRange) {
		if(timeRange.indexOf('-') != -1) {
			String firstTime  = timeRange.substring(0, timeRange.indexOf('-'));
			String secondTime = timeRange.substring(timeRange.indexOf('-') + 1);
			String newTimeRange = String.format("%s - %s", SGTime.convert12To24Hour(firstTime), SGTime.convert12To24Hour(secondTime));
			return newTimeRange;
		}
		return timeRange;
	}
	
	/**
	 * <p>
	 * Reformats a time range String from 24-hour time to 12-hour time format.
	 *
	 * The input time range String is assumed to be in a 24 hour format and stored in the
	 * following format:
	 * </p>
	 * <p>
	 * "H:M-H:M"
	 * </p>
	 * <p>
	 * where H and M can be any length of numeral characters, but are assumed to
	 * represent values between 0 and 23 (inclusive).  The string <i>can</i> have
	 * spaces between any of its members and still be read properly.
	 * </p>
	 *
	 * @param timeRange		the String int 24-hour time range to reformat
	 * @return				the String reformatted to a 12-hour time range
	 */
	static public String convert24To12HourRange(String timeRange) {
		if(timeRange.indexOf('-') != -1) {
			String firstTime  = timeRange.substring(0, timeRange.indexOf('-'));
			String secondTime = timeRange.substring(timeRange.indexOf('-') + 1);
			String newTimeRange = String.format("%s - %s", SGTime.convert24To12Hour(firstTime), SGTime.convert24To12Hour(secondTime));
			return newTimeRange;
		}
		return timeRange;
	}
	
	/**
	 * Compares the beginning times of two SGTimeRanges.
	 *
	 * The function returns the difference between the two times in minutes,
	 * positive if the first start time is later, negative if sooner, and equal
	 * if neither.
	 *
	 * @param a		the SGTimeRange to compare against
	 * @param b		the SGTimeRange to compare to a
	 * @return		the difference between the two start times in minutes
	 */
	static public int compareTimeRangeStarts(SGTimeRange a, SGTimeRange b) {
		return a.start.getMinuteValue() - b.start.getMinuteValue();
	}

	/**
	 * Compares the end times of two SGTimeRanges.
	 *
	 * The function returns the difference between the two times in minutes,
	 * positive if the first end time is later, negative if sooner, and equal
	 * if neither.
	 *
	 * @param a		the SGTimeRange to compare against
	 * @param b		the SGTimeRange to compare to a
	 * @return		the difference between the two end times in minutes
	 */
	static public int compareTimeRangeEnds(SGTimeRange a, SGTimeRange b) {
		return a.end.getMinuteValue() - b.end.getMinuteValue();
	}

	/**
	 * Determines if a given SGTimeRange is within the current instance.
	 *
	 * The function returns true if the ranges are equivalent or if the range
	 * passed as an argument is within the bounds of the current SGTimeRange
	 * times.
	 *
	 * @param a		the SGTimeRange to compare to the current instance
	 * @return		true if the range contains/is equal to the given range, false otherwise
	 */
	public boolean containsRange(SGTimeRange a) {
		boolean start = (compareTimeRangeStarts(this, a) <= 0);
		boolean end   = (compareTimeRangeEnds(this, a) >= 0);
		return start && end;
	}

	/**
	 * <p>
	 * Determines if a given SGTimeRange overlaps the current instance.
	 *
	 * The function returns true if the ranges are equivalent, if the ranges
	 * contain one another, or if any other form of time overlap occurrs.
	 * </p>
	 * <p>
	 * <i>Overlap</i> excludes either time range's start time being equivalent
	 * to the other's end time. For example:
	 * </p>
	 * <p>
	 * "07:30-8:30" and "08:30-09:30"
	 * </p>
	 * <p>
	 * do <b>not</b> overlap.
	 * </p>
	 *
	 * @param a		the SGTimeRange to compare to the current instance
	 * @return		true if the range overlaps the given range, false otherwise
	 */
	public boolean overlapsRange(SGTimeRange a) {
		// If "this" contains "a" or if "a" contains "this" or if they are equivalent
		if(this.containsRange(a) || a.containsRange(this)) {
			return true;
		}
		boolean startsBefore = (compareTimeRangeStarts(this, a) < 0);
		boolean endsBefore   = (compareTimeRangeEnds(this, a)   < 0);
		boolean startsAfter  = (compareTimeRangeStarts(this, a) > 0);
		boolean endsAfter    = (compareTimeRangeEnds(this, a)   > 0);
		boolean crossStart   = this.start.getMinuteValue()      < a.end.getMinuteValue();
		boolean crossEnd     = this.end.getMinuteValue()        > a.start.getMinuteValue();
		// If "this" range ends after range "a" starts
		if(startsBefore && endsBefore && crossEnd) {
			return true;
		}
		// If "this" range starts after range "a" ends
		if(startsAfter && endsAfter && crossStart) {
			return true;
		}
		return false;
	}

	/**
	 * Merge sorts an ArrayList of SGTimeRange objects based on start and
	 * end times.
	 *
	 * The order is first determined by the beginning of the time ranges; however,
	 * if both ranges start at the same time, they are ordered based on their end
	 * times. If the ranges are equivalent, the behavior is unspecified.
	 *
	 * @param timeRanges	the ArrayList of SGTimeRange objects to sort
	 * @param startIndex	the first index of the ArrayList that needs sorting
	 * @param endIndex		the last index (non-inclusive) of the ArrayList that needs sorting
	 */
	static public void mergeSortTimeRangeArrayList(ArrayList<SGTimeRange> timeRanges, int startIndex, int endIndex) {
		if(timeRanges != null && startIndex >= 0 && endIndex <= timeRanges.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				mergeSortTimeRangeArrayList(timeRanges, startIndex, midIndex);
				mergeSortTimeRangeArrayList(timeRanges, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					int compNum = compareTimeRangeStarts(timeRanges.get(i), timeRanges.get(j));
					if(compNum < 0) {
						SGTimeRange temp = timeRanges.remove(i);
						timeRanges.add(startIndex++, temp);
						++i;
					}
					else if(compNum > 0) {
						SGTimeRange temp = timeRanges.remove(j);
						timeRanges.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
					else {
						compNum = compareTimeRangeEnds(timeRanges.get(i), timeRanges.get(j));
						if(compNum < 0) {
							SGTimeRange temp = timeRanges.remove(i);
							timeRanges.add(startIndex++, temp);
							++i;
						}
						else {
							SGTimeRange temp = timeRanges.remove(j);
							timeRanges.add(startIndex++, temp);
							++midIndex;
							++i;
							++j;
						}
					}
				}
				for(; i < midIndex; ++i) {
						SGTimeRange temp = timeRanges.remove(i);
						timeRanges.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						SGTimeRange temp = timeRanges.remove(j);
						timeRanges.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				int compNum = compareTimeRangeStarts(timeRanges.get(startIndex), timeRanges.get(startIndex + 1));
				if(compNum > 0) {
					SGTimeRange a = timeRanges.get(startIndex);
					SGTimeRange b = timeRanges.get(startIndex + 1);
					timeRanges.remove(startIndex);
					timeRanges.remove(startIndex);
					timeRanges.add(startIndex, b);
					timeRanges.add(startIndex + 1, a);
				}
				else if(compNum == 0) {
					compNum = compareTimeRangeEnds(timeRanges.get(startIndex), timeRanges.get(startIndex + 1));
					if(compNum > 0) {
						SGTimeRange a = timeRanges.get(startIndex);
						SGTimeRange b = timeRanges.get(startIndex + 1);
						timeRanges.remove(startIndex);
						timeRanges.remove(startIndex);
						timeRanges.add(startIndex, b);
						timeRanges.add(startIndex + 1, a);
					}
				}
			}
		}
	}
}