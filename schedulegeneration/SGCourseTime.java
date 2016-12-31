package schedulegeneration;

import java.util.ArrayList;

/**
 * <p>
 * An extention of the {@link SGTimeRange} class type that stores a String
 * for the class name along with the corresponding time period of the class.
 *
 * For the {@link schedulegeneration} package, this class is applied to store
 * the input Class time options and associate them with specific classes to
 * keep track which options are still available.
 * </p>
 * <p>
 * While the name field typically contains a subject, number, and qualifier,
 * it is not required by this class to do so.
 * </p>
 * <p>
 * Much like the SGCourseInfo class, there are not many methods included in this
 * class. They deal only with merge sorting based on the start times of the static
 * field {@link timePeriod} and searching based on the {@link className} static field.
 * </p>
 *
 * @see		SGCourseInfo
 * @see		SGTimeRange
 */
public class SGCourseTime {
	/**
	 * Time range that is associated with the Class
	 */
	public SGTimeRange timePeriod;

	/**
	 * Class name that represents the {@link timePeriod}
	 */
	public String className;

	/**
	 * Constructs a new SGCourseTime with the specified timeRange and name.
	 *
	 * @param timeRange		the SGTimeRange of the class time
	 * @param name			the String of the class name
	 */
	public SGCourseTime(SGTimeRange timeRange, String name) {
		timePeriod = timeRange;
		className = name;
	}

	/**
	 * Constructs a new SGCourseTime with the time period given by the times in
	 * the rangeString on the given days and the the specified name.
	 *
	 * @param rangeString	the String of the class time period on a given day
	 * @param days			the String of the days that the class is held
	 * @param name			the String of the class name
	 */
	public SGCourseTime(String rangeString, String days, String name) {
		this(new SGTimeRange(rangeString, days), name);
	}

	/**
	 * Searches an ArrayList of SGCourseTime objects for an instance with a specific
	 * name field, starting with a specified index.
	 *
	 * If a match is not found before reaching the end of the ArrayList, -1 is
	 * returned, following the convention of many other search functions.
	 *
	 * @param classTimes	the ArrayList of the SGCourseTime objects to sort
	 * @param name			the String of the name to find in the ArrayList
	 * @param startIndex	the value of the first index to search
	 * @return				the index of the first found instance with the desired name
	 */
	static public int searchForClassInArrayList(ArrayList<SGCourseTime> classTimes, String name, int startIndex) {
		if(classTimes != null && startIndex >= 0) {
			for(int i = startIndex; i < classTimes.size(); ++i) {
				if(classTimes.get(i).className.compareTo(name) == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Searches an entire ArrayList of SGCourseTime objects for a SGCourseTime with
	 * a matching name String.
	 *
	 * If a match is not found in the ArrayList, -1 is returned, following
	 * the convention of many other search functions.
	 *
	 * @param classTimes	the ArrayList of the SGCourseTime objects to sort
	 * @param name			the String of the name to find in the ArrayList
	 * @return				the index of the first found instance with the desired name
	 */
	static public int searchForClassInArrayList(ArrayList<SGCourseTime> classTimes, String name) {
		return searchForClassInArrayList(classTimes, name, 0);
	}

	/**
	 * Merge sorts an ArrayList of SGCourseTime Objects based on their timePeriod
	 * start times.
	 *
	 * @param classTimes	the ArrayList of SGCourseTime objects to sort
	 * @param startIndex	the first index of the ArrayList that needs sorting
	 * @param endIndex		the last index (non-inclusive) of the ArrayList that needs sorting
	 */
	static public void mergeSortSGClassTimeArrayList(ArrayList<SGCourseTime> classTimes, int startIndex, int endIndex) {
		if(classTimes != null && startIndex >= 0 && endIndex <= classTimes.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				mergeSortSGClassTimeArrayList(classTimes, startIndex, midIndex);
				mergeSortSGClassTimeArrayList(classTimes, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					if(SGTimeRange.compareTimeRangeStarts(classTimes.get(i).timePeriod, classTimes.get(j).timePeriod) < 0) {
						SGCourseTime temp = classTimes.remove(i);
						classTimes.add(startIndex++, temp);
						++i;
					}
					else {
						SGCourseTime temp = classTimes.remove(j);
						classTimes.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
				}
				for(; i < midIndex; ++i) {
						SGCourseTime temp = classTimes.remove(i);
						classTimes.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						SGCourseTime temp = classTimes.remove(j);
						classTimes.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				if(SGTimeRange.compareTimeRangeStarts(classTimes.get(startIndex).timePeriod, classTimes.get(startIndex + 1).timePeriod) > 0) {
					SGCourseTime a = classTimes.get(startIndex);
					SGCourseTime b = classTimes.get(startIndex + 1);
					classTimes.remove(startIndex);
					classTimes.remove(startIndex);
					classTimes.add(startIndex, b);
					classTimes.add(startIndex + 1, a);
				}
			}
		}
	}
}