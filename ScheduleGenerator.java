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
		classTimes = ClassTime.sortClassTimeArrayList(classTimes);
		for(int i = 0; i < classTimes.size(); ++i) {
			ClassTime currClass = classTimes.get(i);
			System.out.format("%10s: %s\t%s\n", currClass.className, currClass.timePeriod.getDays(), ScheduleTimeRange.convert24To12HourRange(currClass.timePeriod.rangeString()));
		}
		for(int j = 0; j < classNames.size(); ++j) {
			System.out.println(classNames.get(j));
			for(int i = ClassTime.searchForClassInArrayList(classTimes, classNames.get(j)); i < classTimes.size(); i = ClassTime.searchForClassInArrayList(classTimes, classNames.get(j), ++i)) {
				if(i >= 0) {
					ClassTime currClass = classTimes.get(i);
					System.out.format("%10s: %s\t%s\n", currClass.className, currClass.timePeriod.getDays(), ScheduleTimeRange.convert24To12HourRange(currClass.timePeriod.rangeString()));
				}
				else {
					break;
				}
			}
		}
	}

	public void generateScheduleWorker(ArrayList<ClassTime> classTimes, ArrayList<String> classNames, int currName) {
		// Create Schedule Object to Send
		for(int i = 0; i < classTimes.size(); ++i) {
			// Add First Class to Schedule Object
			// Recersively Call with next name
		}
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