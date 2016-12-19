// import java.awt.*;

// import java.io.*;

// import java.util.*;
import java.util.ArrayList;

public class Schedule {
	ArrayList<ClassInfo>          classes;
	ArrayList<ArrayList<Integer>> schedule;
	ScheduleTimeRange             dayRange;
	String                        days;
	int                           precisionMinutes;
	int                           numPeriods;

	public Schedule(ScheduleTime start, ScheduleTime end, boolean daysUsed[], ScheduleTime precision) {
		this.days = "";
		for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
			if(daysUsed[i]) {
				this.days = this.days.concat(ScheduleTimeRange.weekdays.substring(i, i + 1));
			}
		}
		int numDays = this.days.length();
		this.precisionMinutes = precision.hour * 60 + precision.minute;
		this.dayRange = new ScheduleTimeRange(String.format("%02d:%02d - %02d:%02d", start.hour, start.minute, end.hour, end.minute), this.days);
		this.dayRange.start = ScheduleTime.roundToPrecision(this.dayRange.start, this.precisionMinutes);
		this.dayRange.end   = ScheduleTime.roundToPrecision(this.dayRange.end, this.precisionMinutes);
		this.numPeriods = this.dayRange.getMinuteLength() / this.precisionMinutes;
		this.schedule = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < numDays; ++i) {
			ArrayList<Integer> day = new ArrayList<Integer>();
			for(int j = 0; j < this.numPeriods; ++j) {
				day.add(0);
			}
			this.schedule.add(day);
		}
		this.classes = new ArrayList<ClassInfo>();
	}

	public void printIntegerSchedule() {
		System.out.format("Days on Schedule: %s\n", this.days);
		System.out.format("Full School Day Range: %s\n", ScheduleTimeRange.convert24To12HourRange(this.dayRange.rangeString()));
		System.out.format("Class Key:\n");
		for(int i = 0; i < this.classes.size(); ++i) {
			System.out.format("%2d)\t%s\n", this.classes.get(i).number, this.classes.get(i).name);
		}
		for(int i = 0; i < this.numPeriods; ++i) {
			for(int j = 0; j < this.days.length() && j < this.schedule.size(); ++j) {
				if(i < this.schedule.get(j).size()) {
					System.out.format("\t%d", this.schedule.get(j).get(i));
				}
			}
			System.out.println("");
		}
	}

	public int getTimeRangeStartPos(ScheduleTimeRange timePeriod) {
		int pos = (timePeriod.start.getMinuteValue() - this.dayRange.start.getMinuteValue()) / this.precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= this.numPeriods) {
			pos = this.numPeriods - 1;
		}
		return pos;
	}

	public int getTimeRangeEndPos(ScheduleTimeRange timePeriod) {
		int pos = this.numPeriods - 1 - (this.dayRange.end.getMinuteValue() - timePeriod.end.getMinuteValue()) / this.precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= this.numPeriods) {
			pos = this.numPeriods - 1;
		}
		return pos;
	}

	public boolean addClass(ClassTime classTime, Integer classNum) {
		// Round Range Start and Ends to Specified Precision
		classTime.timePeriod.start = ScheduleTime.roundToPrecision(classTime.timePeriod.start, this.precisionMinutes);
		classTime.timePeriod.end   = ScheduleTime.roundToPrecision(classTime.timePeriod.end, this.precisionMinutes);
		String classDays = classTime.timePeriod.getDays();
		boolean overlap = false;
		boolean failure = true;
		int startPos = getTimeRangeStartPos(classTime.timePeriod);
		int endPos   = getTimeRangeEndPos(classTime.timePeriod);
		int i = 0;
		for(i = 0; i < this.days.length() && i < this.schedule.size(); ++i) {
			if(classDays.indexOf(this.days.charAt(i)) != -1) {
				failure = false;
				int j = startPos;
				for(j = startPos; j <= endPos && j < this.schedule.get(i).size(); ++j) {
					if(this.schedule.get(i).get(j) == 0) {
						this.schedule.get(i).remove(j);
						this.schedule.get(i).add(j, classNum);
					}
					else {
						overlap = true;
						break;
					}
				}
				if(overlap) {
					for(j = --j; j >= startPos && j >= 0; --j) {
						this.schedule.get(i).remove(j);
						this.schedule.get(i).add(j, 0);
					}
				}
			}
			if(overlap) {
				break;
			}
		}
		if(overlap) {
			for(i = --i; i >= 0; --i) {
				if(classDays.indexOf(this.days.charAt(i)) != -1) {
					int j = startPos;
					for(j = startPos; j <= endPos && j < this.schedule.get(i).size(); ++j) {
						this.schedule.get(i).remove(j);
						this.schedule.get(i).add(j, 0);
					}
				}
			}
			failure = true;
		}
		if(!failure) {
			this.classes.add(new ClassInfo(classTime, classNum));
			this.classes = ClassInfo.mergeSortClassInfoArrayList(this.classes, 0, this.classes.size());
		}
		return !failure;
	}

	public void removeClass(ClassTime classTime, String className, int classNum) {
		// Round Range Start and Ends to Specified Precision
		classTime.timePeriod.start = ScheduleTime.roundToPrecision(classTime.timePeriod.start, this.precisionMinutes);
		classTime.timePeriod.end   = ScheduleTime.roundToPrecision(classTime.timePeriod.end, this.precisionMinutes);
		String classDays = classTime.timePeriod.getDays();
		int startPos = getTimeRangeStartPos(classTime.timePeriod);
		int endPos   = getTimeRangeEndPos(classTime.timePeriod);
		for(int i = 0; i < this.days.length() && i < this.schedule.size(); ++i) {
			if(classDays.indexOf(this.days.charAt(i)) != -1) {
				for(int j = startPos; j <= endPos && j < this.schedule.get(i).size(); ++j) {
					this.schedule.get(i).remove(j);
					this.schedule.get(i).add(j, 0);
				}
			}
		}
		// Remove Class Info
		this.classes.remove(ClassInfo.searchClassInfoArrayList(this.classes, className, classNum));
	}
}