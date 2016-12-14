import java.awt.*;
import java.io.*;
import java.util.*;

public class ScheduleGeneratorTest {
	public static void main( String []args ) {
		try {
			ScheduleGenerator.generateSchedule("input.txt");
		} catch (Exception e) {
			System.err.format("%s%n", e);
		}

		// ScheduleTimeRange dayRange = new ScheduleTimeRange("07:30 am - 05:20 pm", "MWF");
		// boolean daysUsed[] = {false, true, false, true, false, true, false};
		// ScheduleTime precision = new ScheduleTime("00:15");
		// Schedule test = new Schedule(dayRange.start, dayRange.end, daysUsed, precision);
		// System.out.println("Schedule");
		// test.printIntegerSchedule();
		// System.out.println("");
		// ClassTime testClass = new ClassTime("07:30 am - 8:20 am", "MWF", "ECE Test");
		// test.addClass(testClass, 1);
		// System.out.println("Schedule");
		// test.printIntegerSchedule();
		// System.out.println("");
		// ClassTime testClass2 = new ClassTime("07:30 am - 8:25 am", "TR", "ECE Test2");
		// test.addClass(testClass2, 2);
		// System.out.println("Schedule");
		// test.printIntegerSchedule();
		// System.out.println("");
	}
}