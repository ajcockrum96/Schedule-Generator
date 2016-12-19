// import java.awt.*;

// import java.io.*;

// import java.util.*;
import java.util.ArrayList;

public class ScheduleTimeRange {
	static String weekdays = "SMTWRFA";
	boolean       daysUsed[] = {false, false, false, false, false, false, false};
	ScheduleTime  start;
	ScheduleTime  end;
	
	public ScheduleTimeRange(String timeRange, String days) {
		if(timeRange.indexOf("am") != -1 || timeRange.indexOf("pm") != -1) {
			timeRange = convert12To24HourRange(timeRange);
		}
		this.start = new ScheduleTime(timeRange.substring(0, timeRange.indexOf('-')).trim());
		this.end   = new ScheduleTime(timeRange.substring(timeRange.indexOf('-') + 1).trim());
		for(int i = 0; i < weekdays.length(); ++i) {
			if(days.indexOf(weekdays.charAt(i)) != -1) {
				this.daysUsed[i] = true;
			}
		}
	}

	public ScheduleTimeRange(ScheduleTime start, ScheduleTime end) {
		this(String.format("%d:%d - %d:%d", start.hour, start.minute, end.hour, end.minute));
	}

	public ScheduleTimeRange(ScheduleTimeRange timeRange) {
		this(timeRange.rangeString(), timeRange.getDays());
	}
	
	public ScheduleTimeRange(String timeRange) {
		this(timeRange, "");
	}
	
	public ScheduleTimeRange() {
		this("00:00 - 23:59", "");
	}
	
	public String rangeString() {
		return String.format("%02d:%02d - %02d:%02d", this.start.hour, this.start.minute, this.end.hour, this.end.minute);
	}
	
	public int getStartHour() {
		return this.start.hour;
	}
	
	public int getStartMinute() {
		return this.start.minute;
	}
	
	public int getEndHour() {
		return this.end.hour;
	}
	
	public int getEndMinute() {
		return this.end.minute;
	}
	
	public double getHourLength() {
		return ((this.end.hour - this.start.hour) + (double)((this.end.minute) - (this.start.minute)) / 60.0);
	}
	
	public int getMinuteLength() {
		return ((this.end.hour - this.start.hour) * 60 + (this.end.minute - this.start.minute));
	}

	public String getDays() {
		String days = "";
		for(int i = 0; i < weekdays.length(); ++i) {
			if(this.daysUsed[i]) {
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

	public boolean containsRange(ScheduleTimeRange a) {
		boolean start = (compareTimeRangeStarts(this, a) <= 0);
		boolean end   = (compareTimeRangeEnds(this, a) >= 0);
		return start && end;
	}

	public boolean overlapsRange(ScheduleTimeRange a) {
		boolean beforeStart = (compareTimeRangeStarts(this, a) < 0);
		boolean beforeEnd   = (compareTimeRangeEnds(this, a) < 0);
		boolean afterStart  = (compareTimeRangeStarts(this, a) > 0);
		boolean afterEnd    = (compareTimeRangeEnds(this, a) > 0);
		boolean crossStart  = false;
		if(this.getStartHour() != a.getEndHour()) {
			crossStart = (this.getStartHour() < a.getEndHour());
		}
		else {
			crossStart = (this.getStartMinute() < a.getEndMinute());
		}
		boolean crossEnd    = false;
		if(this.getEndHour() != a.getStartHour()) {
			crossEnd = (this.getEndHour() > a.getStartHour());
		}
		else {
			crossEnd = (this.getEndMinute() > a.getStartMinute());
		}
		return ((beforeStart && afterEnd) || (afterStart && beforeEnd) || (beforeStart && beforeEnd && crossEnd) || (afterStart && afterEnd && crossStart) || (compareTimeRangeStarts(this, a) == 0 || compareTimeRangeEnds(this, a) == 0));
	}

	// BUBBLE SORT INEFFICIENT; REDO WHEN POSSIBLE
	static public ArrayList<ScheduleTimeRange> sortTimeRangeArrayList(ArrayList<ScheduleTimeRange> timeRanges) {
		if(timeRanges != null) {
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
		}
		return timeRanges;
	}

	// Merge Sort Implementation
	static public ArrayList<ScheduleTimeRange> mergeSortTimeRangeArrayList(ArrayList<ScheduleTimeRange> timeRanges, int startIndex, int endIndex) {
		if(timeRanges != null && startIndex >= 0 && endIndex <= timeRanges.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				timeRanges = mergeSortTimeRangeArrayList(timeRanges, startIndex, midIndex);
				timeRanges = mergeSortTimeRangeArrayList(timeRanges, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					int compNum = compareTimeRangeStarts(timeRanges.get(i), timeRanges.get(j));
					if(compNum < 0) {
						ScheduleTimeRange temp = timeRanges.remove(i);
						timeRanges.add(startIndex++, temp);
						++i;
					}
					else if(compNum > 0) {
						ScheduleTimeRange temp = timeRanges.remove(j);
						timeRanges.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
					else {
						compNum = compareTimeRangeEnds(timeRanges.get(i), timeRanges.get(j));
						if(compNum < 0) {
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
				}
				for(; i < midIndex; ++i) {
						ScheduleTimeRange temp = timeRanges.remove(i);
						timeRanges.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						ScheduleTimeRange temp = timeRanges.remove(j);
						timeRanges.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				int compNum = compareTimeRangeStarts(timeRanges.get(startIndex), timeRanges.get(startIndex + 1));
				if(compNum > 0) {
					ScheduleTimeRange a = timeRanges.get(startIndex);
					ScheduleTimeRange b = timeRanges.get(startIndex + 1);
					timeRanges.remove(startIndex);
					timeRanges.remove(startIndex);
					timeRanges.add(startIndex, b);
					timeRanges.add(startIndex + 1, a);
				}
				else if(compNum == 0) {
					compNum = compareTimeRangeEnds(timeRanges.get(startIndex), timeRanges.get(startIndex + 1));
					if(compNum > 0) {
						ScheduleTimeRange a = timeRanges.get(startIndex);
						ScheduleTimeRange b = timeRanges.get(startIndex + 1);
						timeRanges.remove(startIndex);
						timeRanges.remove(startIndex);
						timeRanges.add(startIndex, b);
						timeRanges.add(startIndex + 1, a);
					}
				}
			}
		}
		return timeRanges;
	}
}