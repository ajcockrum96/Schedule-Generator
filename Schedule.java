import java.awt.*;
import java.io.*;
import java.util.*;

public class Schedule {
	ArrayList<ClassInfo> classes;
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
		dayRange = new ScheduleTimeRange(String.format("%02d:%02d - %02d:%02d", start.hour, start.minute, end.hour, end.minute), days);
		dayRange.start = ScheduleTime.roundToPrecision(dayRange.start, precisionMinutes);
		dayRange.end   = ScheduleTime.roundToPrecision(dayRange.end, precisionMinutes);
		numPeriods = dayRange.getMinuteLength() / precisionMinutes;

		schedule = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < numDays; ++i) {
			ArrayList<Integer> day = new ArrayList<Integer>();
			for(int j = 0; j < numPeriods; ++j) {
				day.add(0);
			}
			schedule.add(day);
		}
		classes = new ArrayList<ClassInfo>();
	}

	public void printIntegerSchedule() {
		System.out.format("Days on Schedule: %s\n", days);
		System.out.format("Full School Day Range: %s\n", ScheduleTimeRange.convert24To12HourRange(dayRange.rangeString()));
		System.out.format("Class Key:\n");
		for(int i = 0; i < classes.size(); ++i) {
			System.out.format("%2d)\t%s\n", classes.get(i).number, classes.get(i).name);
		}
		for(int i = 0; i < numPeriods; ++i) {
			for(int j = 0; j < days.length(); ++j) {
				System.out.format("\t%d", schedule.get(j).get(i));
			}
			System.out.println("");
		}
	}

	public int getTimeRangeStartPos(ScheduleTimeRange timePeriod) {
		int pos = (timePeriod.start.getMinuteValue() - dayRange.start.getMinuteValue()) / precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= numPeriods) {
			pos = numPeriods - 1;
		}
		return pos;
	}

	public int getTimeRangeEndPos(ScheduleTimeRange timePeriod) {
		int pos = numPeriods - 1 - (dayRange.end.getMinuteValue() - timePeriod.end.getMinuteValue()) / precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= numPeriods) {
			pos = numPeriods - 1;
		}
		return pos;
	}

	public boolean addClass(ClassTime classTime, Integer classNum) {
		// Round Range Start and Ends to Specified Precision
		classTime.timePeriod.start = ScheduleTime.roundToPrecision(classTime.timePeriod.start, precisionMinutes);
		classTime.timePeriod.end   = ScheduleTime.roundToPrecision(classTime.timePeriod.end, precisionMinutes);
		String classDays = classTime.timePeriod.getDays();
		boolean overlap = false;
		boolean failure = true;
		int startPos = getTimeRangeStartPos(classTime.timePeriod);
		int endPos   = getTimeRangeEndPos(classTime.timePeriod);
		int i = 0;
		for(i = 0; i < days.length(); ++i) {
			if(classDays.indexOf(days.charAt(i)) != -1) {
				failure = false;
				int j = startPos;
				for(j = startPos; j <= endPos; ++j) {
					if(schedule.get(i).get(j) == 0) {
						schedule.get(i).remove(j);
						schedule.get(i).add(j, classNum);
					}
					else {
						overlap = true;
						break;
					}
				}
				if(overlap) {
					for(j = --j; j >= startPos; --j) {
						schedule.get(i).remove(j);
						schedule.get(i).add(j, 0);
					}
				}
			}
			if(overlap) {
				break;
			}
		}
		if(overlap) {
			for(i = --i; i >= 0; --i) {
				if(classDays.indexOf(days.charAt(i)) != -1) {
					int j = startPos;
					for(j = startPos; j <= endPos; ++j) {
						schedule.get(i).remove(j);
						schedule.get(i).add(j, 0);
					}
				}
			}
			failure = true;
		}
		if(!failure) {
			classes.add(new ClassInfo(classTime, classNum));
			classes = ClassInfo.mergeSortClassInfoArrayList(classes, 0, classes.size());
		}
		return !failure;
	}

	public void removeClass(ClassTime classTime, String className, int classNum) {
		// Round Range Start and Ends to Specified Precision
		classTime.timePeriod.start = ScheduleTime.roundToPrecision(classTime.timePeriod.start, precisionMinutes);
		classTime.timePeriod.end   = ScheduleTime.roundToPrecision(classTime.timePeriod.end, precisionMinutes);
		String classDays = classTime.timePeriod.getDays();
		int startPos = getTimeRangeStartPos(classTime.timePeriod);
		int endPos   = getTimeRangeEndPos(classTime.timePeriod);
		for(int i = 0; i < days.length(); ++i) {
			if(classDays.indexOf(days.charAt(i)) != -1) {
				for(int j = startPos; j <= endPos; ++j) {
					schedule.get(i).remove(j);
					schedule.get(i).add(j, 0);
				}
			}
		}
		// Remove Class Info
		classes.remove(ClassInfo.searchClassInfoArrayList(classes, className, classNum));
	}
}