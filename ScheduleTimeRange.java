import java.awt.*;
import java.io.*;
import java.util.*;

public class ScheduleTimeRange {
	static String weekdays = "SMTWRFA";
	boolean daysUsed[] = {false, false, false, false, false, false, false};
	ScheduleTime start;
	ScheduleTime end;
	
	public ScheduleTimeRange(String timeRange, String days) {
		if(timeRange.indexOf("am") != -1 || timeRange.indexOf("pm") != -1) {
			timeRange = convert12To24HourRange(timeRange);
		}
		start = new ScheduleTime(timeRange.substring(0, timeRange.indexOf('-')).trim());
		end   = new ScheduleTime(timeRange.substring(timeRange.indexOf('-') + 1).trim());
		for(int i = 0; i < weekdays.length(); ++i) {
			if(days.indexOf(weekdays.charAt(i)) != -1) {
				daysUsed[i] = true;
			}
		}
	}
	
	public ScheduleTimeRange(String days) {
		this("00:00 - 23:59", days);
	}
	
	public String rangeString() {
		return String.format("%02d:%02d - %02d:%02d", start.hour, start.minute, end.hour, end.minute);
	}
	
	public int getStartHour() {
		return start.hour;
	}
	
	public int getStartMinute() {
		return start.minute;
	}
	
	public int getEndHour() {
		return end.hour;
	}
	
	public int getEndMinute() {
		return end.minute;
	}
	
	public double getHourLength() {
		return ((end.hour - start.hour) + (double)((end.minute) - (start.minute)) / 60.0);
	}
	
	public int getMinuteLength() {
		return ((end.hour - start.hour) * 60 + (end.minute - start.minute));
	}

	public String getDays() {
		String days = "";
		for(int i = 0; i < weekdays.length(); ++i) {
			if(daysUsed[i]) {
				days = days.concat(weekdays.substring(i, i + 1));
			}
		}
		return days;
	}
	
	static public String convert12To24HourRange(String timeRange) {
		if(timeRange.indexOf('-') != -1) {
			String firstTime  = timeRange.substring(0, timeRange.indexOf('-'));
			String secondTime = timeRange.substring(timeRange.indexOf('-') + 1);
			String newTimeRange = String.format("%s - %s", ScheduleTime.convert12To24Hour(firstTime), ScheduleTime.convert12To24Hour(secondTime));
			return newTimeRange;
		}
		return timeRange;
	}
	
	static public String convert24To12HourRange(String timeRange) {
		if(timeRange.indexOf('-') != -1) {
			String firstTime  = timeRange.substring(0, timeRange.indexOf('-'));
			String secondTime = timeRange.substring(timeRange.indexOf('-') + 1);
			String newTimeRange = String.format("%s - %s", ScheduleTime.convert24To12Hour(firstTime), ScheduleTime.convert24To12Hour(secondTime));
			return newTimeRange;
		}
		return timeRange;
	}
	
	static public int compareTimeRangeStarts(ScheduleTimeRange a, ScheduleTimeRange b) {
		if(a.getStartHour() != b.getStartHour()) {
			return a.getStartHour() - b.getStartHour();
		}
		return a.getStartMinute() - b.getStartMinute();
	}

	static public int compareTimeRangeEnds(ScheduleTimeRange a, ScheduleTimeRange b) {
		if(a.getEndHour() != b.getEndHour()) {
			return a.getEndHour() - b.getEndHour();
		}
		return a.getEndMinute() - b.getEndMinute();
	}

	// BUBBLE SORT INEFFICIENT; REDO WHEN POSSIBLE
	static public ArrayList<ScheduleTimeRange> sortTimeRangeArrayList(ArrayList<ScheduleTimeRange> timeRanges) {
		for(int i = 0; i < timeRanges.size() - 1; ++i) {
			for(int j = i + 1; j < timeRanges.size(); ++j) {
				if(compareTimeRangeStarts(timeRanges.get(i), timeRanges.get(j)) > 0) {
					ScheduleTimeRange a = timeRanges.get(i);
					ScheduleTimeRange b = timeRanges.get(j);
					timeRanges.remove(i);
					timeRanges.remove(j - 1);
					timeRanges.add(i, b);
					timeRanges.add(j, a);
				}
			}
		}
		return timeRanges;
	}

	// Merge Sort Implementation
	static public ArrayList<ScheduleTimeRange> mergeSortTimeRangeArrayList(ArrayList<ScheduleTimeRange> timeRanges, int startIndex, int endIndex) {
		int length = endIndex - startIndex;
		if(length > 2) {
			int midIndex = length / 2 + startIndex;
			timeRanges = mergeSortTimeRangeArrayList(timeRanges, startIndex, midIndex);
			timeRanges = mergeSortTimeRangeArrayList(timeRanges, midIndex, endIndex);
			int i, j;
			for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
				if(compareTimeRangeStarts(timeRanges.get(i), timeRanges.get(j)) < 0) {
					ScheduleTimeRange temp = timeRanges.remove(i);
					timeRanges.add(startIndex++, temp);
					++i;
				}
				else {
					ScheduleTimeRange temp = timeRanges.remove(j);
					timeRanges.add(startIndex++, temp);
					++midIndex;
					++i;
					++j;
				}
			}
			while(i < midIndex) {
					ScheduleTimeRange temp = timeRanges.remove(i);
					timeRanges.add(startIndex++, temp);
					++i;
			}
			while(j < endIndex) {
					ScheduleTimeRange temp = timeRanges.remove(j);
					timeRanges.add(startIndex++, temp);
					++j;
			}
		}
		else if(length == 2) {
				if(compareTimeRangeStarts(timeRanges.get(startIndex), timeRanges.get(startIndex + 1)) > 0) {
					ScheduleTimeRange a = timeRanges.get(startIndex);
					ScheduleTimeRange b = timeRanges.get(startIndex + 1);
					timeRanges.remove(startIndex);
					timeRanges.remove(startIndex);
					timeRanges.add(startIndex, b);
					timeRanges.add(startIndex + 1, a);
			}
		}
		return timeRanges;
	}
}