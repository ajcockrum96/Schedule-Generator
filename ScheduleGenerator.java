import java.awt.*;
import java.io.*;
import java.util.*;

public class ScheduleGenerator {
	static public void generateSchedule(String filename) throws Exception {
		// Read Input File
		ArrayList<String> lines;
		try {
			lines = readInputFile(filename);
		}
		catch (Exception e) {
			System.err.format("%s%n", e);
			throw new Exception("generateSchedule constructor failed", e);
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
		classTimes = ClassTime.mergeSortClassTimeArrayList(classTimes, 0, classTimes.size());
		boolean daysUsed[] = {false, false, false, false, false, false, false};
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
		} catch (SecurityException e) {
			System.err.format("%e%n", e);
			throw new Exception("generateSchedule constructor failed", e);
		}
		int numSchedules = generateScheduleWorker(schedule, classTimes, classNames, 0, 0);
		System.out.format("%d Total Schedules Generated\n", numSchedules);
	}

	static public int generateScheduleWorker(Schedule schedule, ArrayList<ClassTime> classTimes, ArrayList<String> classNames, int currName, int scheduleNum) {
		if(currName < classNames.size()) {
			for(int i = ClassTime.searchForClassInArrayList(classTimes, classNames.get(currName)); i < classTimes.size() && i >= 0; i = ClassTime.searchForClassInArrayList(classTimes, classNames.get(currName), ++i)) {
				// Add First Class to Schedule Object
				boolean success = schedule.addClass(classTimes.get(i), currName + 1);
				if(success) {
					// Recersively Call with next name
					scheduleNum = generateScheduleWorker(schedule, classTimes, classNames, currName + 1, scheduleNum);
					// CLEANUP AND REMOVE CLASS FROM SCHEDULE BEFORE CONTINUING
					schedule.removeClass(classTimes.get(i), classNames.get(currName), currName + 1);
				}
			}
		}
		else {
			++scheduleNum;
			String filename = String.format(".\\Images\\%d.png", scheduleNum);
			ScheduleImage image = new ScheduleImage(schedule);
			ScheduleImage.writeImageFile(image, filename);
		}
		return scheduleNum;
	}

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
	static public ArrayList<String> readInputFile() throws Exception {
		return readInputFile("input.txt");
	}
}