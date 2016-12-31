package schedulegeneration;

import java.util.ArrayList;

/**
 * <p>
 * An object containing the information needed to assemble a well constrained
 * schedule output.
 *
 * This class contains a 2D ArrayList of Integers that represent the
 * "periods" throughout the day over specified days, where the length of such
 * "periods" is determined by the static field {@link precisionMinutes}.
 * </p>
 * <p>
 * A Schedule instance also contains an ArrayList of SGCourseInfo objects that
 * function as a key, associating the values in the 2D Integer ArrayList with
 * the names of the courses.
 * </p>
 */
public class Schedule {
	/**
	 * List of courses included in the current Schedule
	 */
	public ArrayList<SGCourseInfo> courses;

	/**
	 * 2D representation of the current Schedule
	 */
	public ArrayList<ArrayList<Integer>> schedule;

	/**
	 * Time Period representation of the entire possible Schedule day
	 */
	public SGTimeRange dayRange;

	/**
	 * String of days present in the Schedule
	 */
	public String days;

	/**
	 * Value representing the size of the "time periods" in the Schedule
	 */
	private final int precisionMinutes;
	
	/**
	 * Number of full periods per Schedule day
	 */
	protected int numPeriods;

	/**
	 * Constructs a new Schedule with specified start and end times, days of
	 * the week, and time period size, with each time period value initialized
	 * to 0.
	 *
	 * The boolean array daysUsed is assumed to correspond with each
	 * non-null character in the string {@link SGTimeRange#weekdays}.
	 *
	 * @param start		the SGTime when the schedule day begins
	 * @param end		the SGTime when the schedule day ends
	 * @param daysUsed	the boolean array of schedule days
	 * @param precision	the String of the course name
	 */
	public Schedule(SGTime start, SGTime end, boolean daysUsed[], SGTime precision) {
		this.days = "";
		for(int i = 0; i < SGTimeRange.weekdays.length(); ++i) {
			if(daysUsed[i]) {
				this.days = this.days.concat(SGTimeRange.weekdays.substring(i, i + 1));
			}
		}
		int numDays = this.days.length();
		this.precisionMinutes = precision.hour * 60 + precision.minute;
		this.dayRange = new SGTimeRange(String.format("%02d:%02d - %02d:%02d", start.hour, start.minute, end.hour, end.minute), this.days);
		this.dayRange.start.roundToPrecision(this.precisionMinutes);
		this.dayRange.end.roundToPrecision(this.precisionMinutes);
		this.numPeriods = this.dayRange.getMinuteLength() / this.precisionMinutes;
		this.schedule = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < numDays; ++i) {
			ArrayList<Integer> day = new ArrayList<Integer>();
			for(int j = 0; j < this.numPeriods; ++j) {
				day.add(0);
			}
			this.schedule.add(day);
		}
		this.courses = new ArrayList<SGCourseInfo>();
	}

	/**
	 * Prints the Integer representation of the schedule to the command line.
	 *
	 * Preceding the Integer data, the schedule days, full day range, and course
	 * key are output to aid in output readability.
	 */
	public void printIntegerSchedule() {
		System.out.format("Days on Schedule: %s\n", this.days);
		System.out.format("Full Day Range: %s\n", SGTimeRange.convert24To12HourRange(this.dayRange.rangeString()));
		System.out.format("Course Key:\n");
		for(int i = 0; i < this.courses.size(); ++i) {
			System.out.format("%2d)\t%s\n", this.courses.get(i).number, this.courses.get(i).name);
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

	/**
	 * Returns the position in the Integer array at which the the specified
	 * time period starts.
	 *
	 * If the time Period starts before the schedule day, 0 is returned.
	 * If the time Period starts after the schedule day, numPeriods - 1 is returned.
	 *
	 * @param timePeriod	the SGTimeRange to locate
	 * @return				position at which the timePeriod starts in the Schedule
	 */
	private int getTimeRangeStartPos(SGTimeRange timePeriod) {
		int pos = (timePeriod.start.getMinuteValue() - this.dayRange.start.getMinuteValue()) / this.precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= this.numPeriods) {
			pos = this.numPeriods - 1;
		}
		return pos;
	}

	/**
	 * Returns the position in the Integer array at which the the specified
	 * time period ends.
	 *
	 * If the time Period ends before the schedule day, 0 is returned.
	 * If the time Period ends after the schedule day, numPeriods - 1 is returned.
	 *
	 * @param timePeriod	the SGTimeRange to locate
	 * @return				position at which the timePeriod ends in the Schedule
	 */
	private int getTimeRangeEndPos(SGTimeRange timePeriod) {
		int pos = this.numPeriods - 1 - (this.dayRange.end.getMinuteValue() - timePeriod.end.getMinuteValue()) / this.precisionMinutes;
		if(pos < 0) {
			pos = 0;
		}
		if(pos >= this.numPeriods) {
			pos = this.numPeriods - 1;
		}
		return pos;
	}

	/**
	 * <p>
	 * Adds the course specified by courseTime and courseNum to the Schedule,
	 * return true if and only if every addition was successful.
	 *
	 * The addition of the course includes the addition to the 2D ArrayList of
	 * Integers, as specified in the {@link SGCourseTime#timePeriod} static field,
	 * and the addition to the ArrayList of SGCourseInfo objects.
	 * </p>
	 * <p>
	 * This method does not make the assumption that the given course will fit in
	 * the schedule and does check for any overlap.  If an overlap occurs, the
	 * course addition is undone, leaving the Schedule instance as it was before.
	 * If this occurs, the method returns false.
	 * </p>
	 *
	 * @param courseTime	the SGCourseTime that represents the new course's time period
	 * @param courseNum		the value to represent the new course in the Schedule
	 * @return				true if addition was successful
	 */
	public boolean addCourse(SGCourseTime courseTime, Integer courseNum) {
		// Round Range Start and Ends to Specified Precision
		courseTime.timePeriod.start.roundToPrecision(this.precisionMinutes);
		courseTime.timePeriod.end.roundToPrecision(this.precisionMinutes);
		String courseDays = courseTime.timePeriod.getDays();
		boolean overlap = false;
		boolean failure = true;
		int startPos = getTimeRangeStartPos(courseTime.timePeriod);
		int endPos   = getTimeRangeEndPos(courseTime.timePeriod);
		int i = 0;
		for(i = 0; i < this.days.length() && i < this.schedule.size(); ++i) {
			if(courseDays.indexOf(this.days.charAt(i)) != -1) {
				failure = false;
				int j = startPos;
				for(j = startPos; j <= endPos && j < this.schedule.get(i).size(); ++j) {
					if(this.schedule.get(i).get(j) == 0) {
						this.schedule.get(i).remove(j);
						this.schedule.get(i).add(j, courseNum);
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
				if(courseDays.indexOf(this.days.charAt(i)) != -1) {
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
			this.courses.add(new SGCourseInfo(courseTime, courseNum));
			SGCourseInfo.mergeSortSGCourseInfoArrayList(this.courses, 0, this.courses.size());
		}
		return !failure;
	}

	/**
	 * <p>
	 * Removes the course specified by courseTime and courseNum from the Schedule.
	 *
	 * The removal of the course includes the removal from the 2D ArrayList of
	 * Integers, as specified in the {@link SGCourseTime#timePeriod} static field,
	 * and the removal of the first matching instances in the ArrayList of
	 * SGCourseInfo objects.
	 * </p>
	 * <p>
	 * This method does make the assumption that the course is already in
	 * the schedule and does not check otherwise.
	 * </p>
	 *
	 * @param courseTime	the SGCourseTime that represents the course's time period
	 * @param courseNum		the value that represents the course in the Schedule
	 */
	public void removeCourse(SGCourseTime courseTime, int courseNum) {
		// Round Range Start and Ends to Specified Precision
		courseTime.timePeriod.start.roundToPrecision(this.precisionMinutes);
		courseTime.timePeriod.end.roundToPrecision(this.precisionMinutes);
		String courseDays = courseTime.timePeriod.getDays();
		int startPos = getTimeRangeStartPos(courseTime.timePeriod);
		int endPos   = getTimeRangeEndPos(courseTime.timePeriod);
		for(int i = 0; i < this.days.length() && i < this.schedule.size(); ++i) {
			if(courseDays.indexOf(this.days.charAt(i)) != -1) {
				for(int j = startPos; j <= endPos && j < this.schedule.get(i).size(); ++j) {
					this.schedule.get(i).remove(j);
					this.schedule.get(i).add(j, 0);
				}
			}
		}
		// Remove Course Info
		this.courses.remove(SGCourseInfo.searchSGCourseInfoArrayList(this.courses, courseTime.courseName, courseNum));
	}
}