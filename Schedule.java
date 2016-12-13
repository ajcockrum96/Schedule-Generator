import java.awt.*;
import java.io.*;
import java.util.*;

public class Schedule {
    String days = "";
    int precisionMinutes;
    public Schedule(ScheduleTime start, ScheduleTime end, boolean daysUsed[], ScheduleTime precision) {
        for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
            if(daysUsed[i]) {
                days = days.concat(ScheduleTimeRange.weekdays.substring(i, i + 1));
            }
        }
        int numDays = days.length();
        precisionMinutes = precision.hour * 60 + precision.minute;
        ScheduleTimeRange dayRange = new ScheduleTimeRange(String.format("%02d:%02d - %02d:%02d", start.hour, start.minute, end.hour, end.minute), days)
        int numPeriods = dayRange.getMinuteLength() / precisionMinutes;

        ArrayList<ArrayList<Integer>> schedule = new ArrayList<ArrayList<Integer>>();
        for(int i = 0; i < numDays; ++i) {
            ArrayList<Integer> day = new ArrayList<Integer>();
            for(int j = 0; j < numPeriods; ++j) {
                day.add(0);
            }
            schedule.add(day);
        }
    }

    public addClass(ClassTime classTime, int classNum) {
        // Round Range Start and Ends to Specified Precision
    }
}