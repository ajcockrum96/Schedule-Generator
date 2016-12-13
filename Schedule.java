import java.awt.*;
import java.io.*;
import java.util.*;

public class Schedule {
	ArrayList<ArrayList<Integer>> schedule;
	ScheduleTimeRange dayRange;
	String days = "";
	int precisionMinutes;
	int numPeriods;
	public Schedule(ScheduleTime start, ScheduleTime end, boolean daysUsed[], ScheduleTime precision) {
		for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
			if(daysUsed[i]) {
				days = days.concat(ScheduleTimeRange.weekdays.substring(i, i + 1));
			}
		}
		int numDays = days.length();
		precisionMinutes = precision.hour * 60 + precision.minute;
		dayRange = new ScheduleTimeRange(String.format("%02d:%02d - %02d:%02d", start.hour, start.minute, end.hour, end.minute), days)
		numPeriods = dayRange.getMinuteLength() / precisionMinutes;

		schedule = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < numDays; ++i) {
			ArrayList<Integer> day = new ArrayList<Integer>();
			for(int j = 0; j < numPeriods; ++j) {
				day.add(0);
			}
			schedule.add(day);
		}
	}

	public getTimeRangeStartPos(ScheduleTimeRange timePeriod) {
		int pos = ScheduleTimeRange.compareTimeRangeStarts(timePeriod, dayRange) * 60 / precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= numPeriods) {
			pos = numPeriods - 1;
		}
		return pos;
	}

	public getTimeRangeEndPos(ScheduleTimeRange timePeriod) {
		int pos = numPeriods - 1 - ScheduleTimeRange.compareTimeRangeEnds(dayRange, timePeriod) * 60 / precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= numPeriods) {
			pos = numPeriods - 1;
		}
		return pos;
	}

	public addClass(ClassTime classTime, int classNum) {
		// Round Range Start and Ends to Specified Precision
		classTime.timePeriod.start = ClassTime.roundToPrecision(classTime.timePeriod.start, precisionMinutes);
		classTime.timePeriod.end   = ClassTime.roundToPrecision(classTime.timePeriod.end, precisionMinutes);
		String classDays = classTime.timePeriod.getDays();
		for(int i = 0; i < days.length(); ++i) {
			if(classDays.indexOf(days.charAt(i)) != -1) {
				int startPos = getTimeRangeStartPos(classTime.timePeriod);
				int endPos   = getTimeRangeEndPos(classTime.timePeriod);
			}
		}
	}
}