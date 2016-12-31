package schedulegeneration;

import java.util.ArrayList;

/**
 * <p>
 * An extention of the {@link SGTimeRange} class type that stores a String
 * for the course name along with the corresponding time period of the course.
 *
 * For the {@link schedulegeneration} package, this class is applied to store
 * the input Course time options and associate them with specific courses to
 * keep track which options are still available.
 * </p>
 * <p>
 * While the name field typically contains a subject, number, and qualifier,
 * it is not required by this class to do so.
 * </p>
 * <p>
 * Much like the SGCourseInfo class, there are not many methods included in this
 * class. They deal only with merge sorting based on the start times of the static
 * field {@link timePeriod} and searching based on the {@link courseName} static field.
 * </p>
 *
 * @see		SGCourseInfo
 * @see		SGTimeRange
 */
public class SGCourseTime {
	/**
	 * Time range that is associated with the Course
	 */
	public SGTimeRange timePeriod;

	/**
	 * Course name that represents the {@link timePeriod}
	 */
	public String courseName;

	/**
	 * Constructs a new SGCourseTime with the specified timeRange and name.
	 *
	 * @param timeRange		the SGTimeRange of the course time
	 * @param name			the String of the course name
	 */
	public SGCourseTime(SGTimeRange timeRange, String name) {
		timePeriod = timeRange;
		courseName = name;
	}

	/**
	 * Constructs a new SGCourseTime with the time period given by the times in
	 * the rangeString on the given days and the the specified name.
	 *
	 * @param rangeString	the String of the course time period on a given day
	 * @param days			the String of the days that the course is held
	 * @param name			the String of the course name
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
	 * @param courseTimes	the ArrayList of the SGCourseTime objects to sort
	 * @param name			the String of the name to find in the ArrayList
	 * @param startIndex	the value of the first index to search
	 * @return				the index of the first found instance with the desired name
	 */
	static public int searchForCourseInArrayList(ArrayList<SGCourseTime> courseTimes, String name, int startIndex) {
		if(courseTimes != null && startIndex >= 0) {
			for(int i = startIndex; i < courseTimes.size(); ++i) {
				if(courseTimes.get(i).courseName.compareTo(name) == 0) {
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
	 * @param courseTimes	the ArrayList of the SGCourseTime objects to sort
	 * @param name			the String of the name to find in the ArrayList
	 * @return				the index of the first found instance with the desired name
	 */
	static public int searchForCourseInArrayList(ArrayList<SGCourseTime> courseTimes, String name) {
		return searchForCourseInArrayList(courseTimes, name, 0);
	}

	/**
	 * Merge sorts an ArrayList of SGCourseTime Objects based on their timePeriod
	 * start times.
	 *
	 * @param courseTimes	the ArrayList of SGCourseTime objects to sort
	 * @param startIndex	the first index of the ArrayList that needs sorting
	 * @param endIndex		the last index (non-inclusive) of the ArrayList that needs sorting
	 */
	static public void mergeSortSGCourseTimeArrayList(ArrayList<SGCourseTime> courseTimes, int startIndex, int endIndex) {
		if(courseTimes != null && startIndex >= 0 && endIndex <= courseTimes.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				mergeSortSGCourseTimeArrayList(courseTimes, startIndex, midIndex);
				mergeSortSGCourseTimeArrayList(courseTimes, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					if(SGTimeRange.compareTimeRangeStarts(courseTimes.get(i).timePeriod, courseTimes.get(j).timePeriod) < 0) {
						SGCourseTime temp = courseTimes.remove(i);
						courseTimes.add(startIndex++, temp);
						++i;
					}
					else {
						SGCourseTime temp = courseTimes.remove(j);
						courseTimes.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
				}
				for(; i < midIndex; ++i) {
						SGCourseTime temp = courseTimes.remove(i);
						courseTimes.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						SGCourseTime temp = courseTimes.remove(j);
						courseTimes.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				if(SGTimeRange.compareTimeRangeStarts(courseTimes.get(startIndex).timePeriod, courseTimes.get(startIndex + 1).timePeriod) > 0) {
					SGCourseTime a = courseTimes.get(startIndex);
					SGCourseTime b = courseTimes.get(startIndex + 1);
					courseTimes.remove(startIndex);
					courseTimes.remove(startIndex);
					courseTimes.add(startIndex, b);
					courseTimes.add(startIndex + 1, a);
				}
			}
		}
	}
}