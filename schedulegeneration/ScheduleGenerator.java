package schedulegeneration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>
 * A class that brute force generates schedules given data from an input file
 * by using the {@link schedulegeneration} package objects and methods.
 *
 * This class contains no instance fields or methods. All methods are static
 * and there is no functioning constructor for this class. <b><u>IT WAS NOT DESIGNED TO
 * BE CONSTRUCTED.</u></b>
 * </p>
 * <p>
 * FIXME: Insert process description here
 * </p>
 * @see Schedule
 * @see ScheduleImage
 * @see SGWindow
 */
public class ScheduleGenerator {
	/**
	 * <p>
	 * This constructor is unusable. <b><u>THIS CLASS WAS NOT DESIGNED TO
	 * BE CONSTRUCTED.</u></b>
	 * </p>
	 */
	private ScheduleGenerator() {
	}

	/**
	 * <p>
	 * Reads in input file data, begins brute force schedule generation process
	 * (including the days from daysGiven and those found in the input file),
	 * and launches FinalWindow object to alert user that the process has finished.
	 * </p>
	 * <p>
	 * This brute force portion reads in the data from the file into lines, then
	 * translates it into ClassTime objects and Strings of the class names. The
	 * file is assumed to always have class times after a given class name; if
	 * this is not the case, unexpected behavior may occur.
	 * </p>
	 * <p>
	 * Then the program dynamically includes days from the input file, adding them
	 * to the days forced to be on the schedule by the parameter String daysGiven.
	 * Next, a directory for the schedule image files is made if it does not
	 * already exist. If it does exist, all files inside will be deleted before
	 * proceeding.
	 * </p>
	 * <p>
	 * The {@link generateScheduleWorker} is then called to recursively call
	 * itself and modify a Schedule instance to generate all possible schedule
	 * options. And once this worker is done, the FinalWindow is launched.
	 * </p>
	 *
	 * @param  filename				the String of the filename to read
	 * @param  daysGiven			the String containing days to force into schedule
	 * @throws Exception			If an I/O error occurs
	 */
	static public void generateSchedule(String filename, String daysGiven) throws Exception {
		System.out.println("Generating");
		// Read Input File
		ArrayList<String> lines;
		try {
			lines = readInputFile(filename);
		}
		catch (Exception e) {
			System.err.format("%s%n", e);
			throw new Exception("generateSchedule failed", e);
		}

		// Create Class Times
		ArrayList<ClassTime> classTimes = new ArrayList<ClassTime>();
		ArrayList<String>    classNames = new ArrayList<String>();
		String className = "";
		for(int i = 0; i < lines.size(); ++i) {
			String currLine = lines.get(i);
			if(currLine.length() > 0) {
				char firstChar = currLine.charAt(0);
				if(Character.isDigit(firstChar)) {
					className = currLine.substring(currLine.indexOf('\t') + 1);
					classNames.add(className);
				}
				else if(ScheduleTimeRange.weekdays.indexOf(firstChar) != -1) {
					String days = currLine.substring(0, currLine.indexOf('\t'));
					String rangeString = currLine.substring(currLine.indexOf('\t'));
					ClassTime classTime = new ClassTime(rangeString, days, className);
					classTimes.add(classTime);
				}
			}
		}
		ClassTime.mergeSortClassTimeArrayList(classTimes, 0, classTimes.size());
		boolean daysUsed[] = {false, false, false, false, false, false, false};
		// Account for given days
		for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
			if(daysGiven.indexOf(ScheduleTimeRange.weekdays.charAt(i)) != -1) {
				daysUsed[i] = true;
			}
		}
		// Dynamically account for days used
		for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
			for(int j = 0; j < classTimes.size(); ++j) {
				if(classTimes.get(j).timePeriod.daysUsed[i]) {
					daysUsed[i] = true;
					break;
				}
			}
		}
		ScheduleTime precision = new ScheduleTime("00:15");
		Schedule schedule = new Schedule(classTimes.get(0).timePeriod.start, classTimes.get(classTimes.size() - 1).timePeriod.end, daysUsed, precision);
		// Open Directory for Image Files
		try {
			File folder = new File(".\\Images");
			if(!folder.isDirectory()) {
				folder.mkdir();
			}
			// Delete old schedules to make way for new ones
			else {
				for(File file: folder.listFiles()) {
					if(!file.isDirectory()) {
						file.delete();
					}
				}
			}
		} catch (SecurityException e) {
			System.err.format("%s%n", e);
			throw new Exception("generateSchedule failed", e);
		}
		int numSchedules = generateScheduleWorker(schedule, classTimes, classNames, 0, 0);
		System.out.format("%d Total Schedules Generated\n", numSchedules);

		try {
			FinalWindow win = new FinalWindow(numSchedules);
		} catch(Exception e) {
			System.err.format("%s%n", e);
		}
	}

	/**
	 * Reads in input file data, begins brute force schedule generation process
	 * (using only the days found in the input file), and launches FinalWindow
	 * object to alert user that the process has finished.
	 *
	 * @param  filename				the String of the filename to read
	 * @throws Exception			If an I/O error occurs
	 */
	static public void generateSchedule(String filename) throws Exception {
		generateSchedule(filename, "");
	}

	/**
	 * <p>
	 * Adds the next non-overlapping time option for the given class to the
	 * given schedule, recursively calling to add the next class in given list
	 * of class names.
	 * </p>
	 * <p>
	 * If the given class is past the end of the list of class names, the schedule
	 * is deemed "finished", a ScheduleImage is generated for the schedule, the
	 * image is written to a file based on the now incremented schedule number, and
	 * the function returns.
	 * </p>
	 * <p>
	 * When the first schedule is generated, the image key is generated using
	 * {@link ScheduleImage#writeImageKey} method.
	 * </p>
	 * <p>
	 * When the recursive call returns each time, the next time option for the
	 * given class is found. If no more options exist, the function returns.
	 * </p>
	 *
	 * @param  schedule				the Schedule instance to modify
	 * @param  classTimes			the ArrayList of ClassTime objects
	 * @param  classNames			the ArrayList of class names
	 * @param  currName				the index of the current class name
	 * @param  scheduleNum			the current number of schedules generated
	 * @return						the new number of schedules generated
	 */
	static private int generateScheduleWorker(Schedule schedule, ArrayList<ClassTime> classTimes, ArrayList<String> classNames, int currName, int scheduleNum) {
		if(currName < classNames.size()) {
			for(int i = ClassTime.searchForClassInArrayList(classTimes, classNames.get(currName)); i < classTimes.size() && i >= 0; i = ClassTime.searchForClassInArrayList(classTimes, classNames.get(currName), ++i)) {
				// Add First Class to Schedule Object
				boolean success = schedule.addClass(classTimes.get(i), currName + 1);
				if(success) {
					// Recursively Call with next name
					scheduleNum = generateScheduleWorker(schedule, classTimes, classNames, currName + 1, scheduleNum);
					// Cleanup and remove class from schedule before continuing
					schedule.removeClass(classTimes.get(i), currName + 1);
				}
			}
		}
		else {
			++scheduleNum;
			String filename = String.format(".\\Images\\%d.png", scheduleNum);
			ScheduleImage image = new ScheduleImage(schedule);
			ScheduleImage.writeImageFile(image, filename);
			// Output Key
			if(scheduleNum == 1) {
				ScheduleImage.writeImageKey(".\\Images\\Key.png", schedule.classes);
			}
		}
		return scheduleNum;
	}

	/**
	 * <p>
	 * Reads and stores a given input file in an ArrayList of line strings.
	 * </p>
	 *
	 * @param  filename			the String of the filename to read
	 * @return					the ArrayList of line Strings
	 * @throws Exception		If an I/O error occurs
	 */
	static public ArrayList<String> readInputFile(String filename) throws Exception {
		ArrayList<String> lines = new ArrayList<String>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			String line = null;
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			System.err.format("%s%n", e);
			throw new Exception("readFileLines failed", e);
		}
		return lines;
	}

	/**
	 * <p>
	 * Reads and stores the file "input.txt" in an ArrayList of line strings.
	 * </p>
	 *
	 * @return					the ArrayList of line Strings
	 * @throws Exception		If an I/O error occurs
	 */
	static public ArrayList<String> readInputFile() throws Exception {
		return readInputFile("input.txt");
	}
}