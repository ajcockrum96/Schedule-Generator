import java.awt.*;
import java.io.*;
import java.util.*;

public class ClassTime {
	ScheduleTimeRange timePeriod;
	String className;
	public ClassTime(ScheduleTimeRange timeRange, String name) {
		timePeriod = timeRange;
		className = name;
	}

	public ClassTime(String rangeString, String days, String name) {
		this(new ScheduleTimeRange(rangeString, days), name);
	}

	static public int searchForClassInArrayList(ArrayList<ClassTime> classTimes, String name, int startIndex) {
		for(int i = startIndex; i < classTimes.size(); ++i) {
			if(String.compareTo(classTimes.get(i).className, name) == 0) {
				return i;
			}
		}
		return -1;
	}
	static public int searchForClassInArrayList(ArrayList<ClassTime> classTimes, String name) {
		return searchForClassInArrayList(classTimes, name, 0);
	}

	// BUBBLE SORT INEFFICIENT; REDO WHEN POSSIBLE
	static public ArrayList<ClassTime> sortClassTimeArrayList(ArrayList<ClassTime> classTimes) {
		for(int i = 0; i < classTimes.size() - 1; ++i) {
			for(int j = i + 1; j < classTimes.size(); ++j) {
				if(compareTimeRangeStarts(classTimes.get(i).timePeriod, classTimes.get(j).timePeriod) > 0) {
					ClassTime a = classTimes.get(i);
					ClassTime b = classTimes.get(j);
					classTime.remove(i);
					classTime.remove(j - 1);
					classTime.add(i, b);
					classTime.add(j, a);
				}
			}
		}
		return classTimes;
	}
}