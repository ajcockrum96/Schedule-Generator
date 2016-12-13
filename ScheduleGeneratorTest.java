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
	}
}