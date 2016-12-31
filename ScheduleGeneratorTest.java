import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;

public class ScheduleGeneratorTest {
	public static void main( String []args ) {
		try {
			ScheduleGenerator.generateSchedule("input1.txt");
		} catch (Exception e) {
			System.err.format("%s%n", e);
		}

		// SGTimeRange dayRange = new SGTimeRange("07:30 am - 05:20 pm", "MTWRF");
		// boolean daysUsed[] = {false, true, true, true, true, true, false};
		// SGTime precision = new SGTime("00:15");
		// Schedule test = new Schedule(dayRange.start, dayRange.end, daysUsed, precision);
		// System.out.println("Schedule");
		// test.printIntegerSchedule();
		// System.out.println("");
		// SGClassTime testClass = new SGClassTime("07:30 am - 8:20 am", "MWF", "ECE Test");
		// test.addClass(testClass, 1);
		// System.out.println("Schedule");
		// test.printIntegerSchedule();
		// System.out.println("");
		// SGClassTime testClass2 = new SGClassTime("07:30 am - 8:25 am", "TR", "ECE Test2");
		// test.addClass(testClass2, 2);
		// System.out.println("Schedule");
		// test.printIntegerSchedule();
		// System.out.println("");
		// JFrame win = new JFrame("Test Image");
		// win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		// win.setResizable( false );
		// win.setSize( test.days.length() * 20, test.numPeriods * 20 );
		// SGImage image = new SGImage(test);
		// win.add(new JLabel(new ImageIcon(image.image)));
		// win.setVisible( true );
	}
}