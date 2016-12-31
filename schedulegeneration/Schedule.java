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
 * A Schedule instance also contains an ArrayList of SGClassInfo objects that
 * function as a key, associating the values in the 2D Integer ArrayList with
 * the names of the classes.
 * </p>
 */
public class Schedule {
	/**
	 * List of classes included in the current Schedule
	 */
	public ArrayList<SGClassInfo> classes;

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
	 * @param precision	the String of the class name
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
		this.classes = new ArrayList<SGClassInfo>();
	}

	/**
	 * Prints the Integer representation of the schedule to the command line.
	 *
	 * Preceding the Integer data, the schedule days, full day range, and class
	 * key are output to aid in output readability.
	 */
	public void printIntegerSchedule() {
		System.out.format("Days on Schedule: %s\n", this.days);
		System.out.format("Full Day Range: %s\n", SGTimeRange.convert24To12HourRange(this.dayRange.rangeString()));
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
	 * Adds the class specified by classTime and classNum to the Schedule,
	 * return true if and only if every addition was successful.
	 *
	 * The addition of the class includes the addition to the 2D ArrayList of
	 * Integers, as specified in the {@link SGClassTime#timePeriod} static field,
	 * and the addition to the ArrayList of SGClassInfo objects.
	 * </p>
	 * <p>
	 * This method does not make the assumption that the given class will fit in
	 * the schedule and does check for any overlap.  If an overlap occurs, the
	 * class addition is undone, leaving the Schedule instance as it was before.
	 * If this occurs, the method returns false.
	 * </p>
	 *
	 * @param classTime	the SGClassTime that represents the new class's time period
	 * @param classNum	the value to represent the new class in the Schedule
	 * @return			true if addition was successful
	 */
	public boolean addClass(SGClassTime classTime, Integer classNum) {
		// Round Range Start and Ends to Specified Precision
		classTime.timePeriod.start.roundToPrecision(this.precisionMinutes);
		classTime.timePeriod.end.roundToPrecision(this.precisionMinutes);
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
			this.classes.add(new SGClassInfo(classTime, classNum));
			SGClassInfo.mergeSortSGClassInfoArrayList(this.classes, 0, this.classes.size());
		}
		return !failure;
	}

	/**
	 * <p>
	 * Removes the class specified by classTime and classNum from the Schedule.
	 *
	 * The removal of the class includes the removal from the 2D ArrayList of
	 * Integers, as specified in the {@link SGClassTime#timePeriod} static field,
	 * and the removal of the first matching instances in the ArrayList of
	 * SGClassInfo objects.
	 * </p>
	 * <p>
	 * This method does make the assumption that the class is already in
	 * the schedule and does not check otherwise.
	 * </p>
	 *
	 * @param classTime	the SGClassTime that represents the class's time period
	 * @param classNum	the value that represents the class in the Schedule
	 */
	public void removeClass(SGClassTime classTime, int classNum) {
		// Round Range Start and Ends to Specified Precision
		classTime.timePeriod.start.roundToPrecision(this.precisionMinutes);
		classTime.timePeriod.end.roundToPrecision(this.precisionMinutes);
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
		this.classes.remove(SGClassInfo.searchSGClassInfoArrayList(this.classes, classTime.className, classNum));
	}
}