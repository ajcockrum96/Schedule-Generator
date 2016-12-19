// import java.awt.*;

// import java.io.*;

// import java.util.*;
import java.util.ArrayList;

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
		if(classTimes != null && startIndex >= 0) {
			for(int i = startIndex; i < classTimes.size(); ++i) {
				if(classTimes.get(i).className.compareTo(name) == 0) {
					return i;
				}
			}
		}
		return -1;
	}
	static public int searchForClassInArrayList(ArrayList<ClassTime> classTimes, String name) {
		return searchForClassInArrayList(classTimes, name, 0);
	}

	// BUBBLE SORT INEFFICIENT; REDO WHEN POSSIBLE
	static public ArrayList<ClassTime> sortClassTimeArrayList(ArrayList<ClassTime> classTimes) {
		if(classTimes != null) {
			for(int i = 0; i < classTimes.size() - 1; ++i) {
				for(int j = i + 1; j < classTimes.size(); ++j) {
					if(ScheduleTimeRange.compareTimeRangeStarts(classTimes.get(i).timePeriod, classTimes.get(j).timePeriod) > 0) {
						ClassTime a = classTimes.get(i);
						ClassTime b = classTimes.get(j);
						classTimes.remove(i);
						classTimes.remove(j - 1);
						classTimes.add(i, b);
						classTimes.add(j, a);
					}
				}
			}
		}
		return classTimes;
	}

	// Merge Sort Implementation
	static public ArrayList<ClassTime> mergeSortClassTimeArrayList(ArrayList<ClassTime> classTimes, int startIndex, int endIndex) {
		if(classTimes != null && startIndex >= 0 && endIndex <= classTimes.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				classTimes = mergeSortClassTimeArrayList(classTimes, startIndex, midIndex);
				classTimes = mergeSortClassTimeArrayList(classTimes, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					if(ScheduleTimeRange.compareTimeRangeStarts(classTimes.get(i).timePeriod, classTimes.get(j).timePeriod) < 0) {
						ClassTime temp = classTimes.remove(i);
						classTimes.add(startIndex++, temp);
						++i;
					}
					else {
						ClassTime temp = classTimes.remove(j);
						classTimes.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
				}
				for(; i < midIndex; ++i) {
						ClassTime temp = classTimes.remove(i);
						classTimes.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						ClassTime temp = classTimes.remove(j);
						classTimes.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				if(ScheduleTimeRange.compareTimeRangeStarts(classTimes.get(startIndex).timePeriod, classTimes.get(startIndex + 1).timePeriod) > 0) {
					ClassTime a = classTimes.get(startIndex);
					ClassTime b = classTimes.get(startIndex + 1);
					classTimes.remove(startIndex);
					classTimes.remove(startIndex);
					classTimes.add(startIndex, b);
					classTimes.add(startIndex + 1, a);
				}
			}
		}
		return classTimes;
	}
}