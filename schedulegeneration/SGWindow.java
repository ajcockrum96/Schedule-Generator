package schedulegeneration;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.lang.SecurityException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * <p>
 * A window wrapper that launches the Schedule Generation program contained within
 * the {@link schedulegeneration} package.
 * </p>
 * <p>
 * The methods contained within this class are intended for internal usage by
 * the constructor and the ActionListener. The only method to be called externally
 * is the constructor, and from that point forward, user interaction will
 * progress the program.
 * </p>
 * @see ScheduleGenerator
 * @see SGCheckBoxGrid
 */
public class SGWindow implements ActionListener {
	/**
	 * Days included in the schedule
	 */
	private String days;

	/**
	 * The check boxes for inputting schedule preferences
	 */
	private SGCheckBoxGrid checkBoxes;

	/**
	 * Compiled list of course time period options
	 */
	private ArrayList<SGCourseTime> courseTimes;

	/**
	 * Prompt window
	 */
	private JFrame win;

	/**
	 * Filename for the filtered input file
	 */
	private String newFilename;

	/**
	 * <p>
	 * Creates a new SGWindow instance and launches the Schedule Generation
	 * program with the given input and filtered input filenames.
	 * </p>
	 * <p>
	 * This Schedule Generation program starts by reading in the given input
	 * filename as lines. Then a list of SGCourseTime objects is obtained from the
	 * lines and a compiled list of SGTimeRange objects is obtained from
	 * those.
	 * </p>
	 * <p>
	 * Using these time ranges, an {@link SGCheckBoxGrid} instance is created, and the
	 * preference window is launched with the SGCheckBoxGrid included.
	 * </p>
	 * <p>
	 * From this point forward, the SGCheckBoxGrid handles the check boxes and
	 * and the SGWindow ActionListener handles the remainder of the Schedule
	 * Generation Process.
	 * </p>
	 *
	 * @param  inputFilename	FIXME
	 * @param  prefFilename		FIXME
	 * @throws Exception		FIXME: If the input could not be read, JFrame could not be constructed, or if JFrame.EXIT_ON_CLOSE causes a SecurityException
	 */
	public SGWindow(String inputFilename, String prefFilename) throws Exception {
		this.newFilename   = prefFilename;
		// Read File Lines
		ArrayList<String> lines;
		try {
			lines = ScheduleGenerator.readInputFile(inputFilename);
		} catch (Exception e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		}
		
		// Get Times from Input Lines as Courses
		getSGCourseTimes(lines);

		// Get Times from Input Courses as Compiled List
		ArrayList<SGTimeRange> timeRanges = getDayTimes(this.courseTimes);
		int dayLength = SGTimeRange.compareTimeRangeStarts(timeRanges.get(timeRanges.size() - 1), timeRanges.get(0)) / 60;
		determineDaysUsed(timeRanges);

		// Set up check boxes
		this.checkBoxes = new SGCheckBoxGrid(timeRanges, this.days);

		// Initialize Window
		try {
			this.win = new JFrame("Schedule Generator");
			this.win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		} catch (HeadlessException e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		} catch (SecurityException e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		}
		this.win.setResizable( false );
		 
		this.win.setSize( this.days.length() * 200, this.checkBoxes.dayRange * 50 + 50 );
		this.win.setLayout( new GridBagLayout() );

		GridBagConstraints c = new GridBagConstraints();
		c.gridx      = 0;
		c.gridy      = 0;
		c.gridwidth  = this.days.length();
		c.gridheight = this.checkBoxes.dayRange;
		c.weightx    = 1;
		c.weighty    = 1;
		c.ipadx      = (int)(this.win.getSize().getWidth()) / (2 * this.days.length());
		c.ipady      = (int)(this.win.getSize().getHeight()) / (2 * this.checkBoxes.dayRange);
		c.fill       = GridBagConstraints.BOTH;
		this.win.add(this.checkBoxes.fullGrid, c);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		c.gridx      = 0;
		c.gridy      = c.gridheight;
		c.gridwidth  = this.days.length();
		c.gridheight = 1;
		c.weightx    = 0;
		c.weighty    = 0;
		c.fill       = GridBagConstraints.NONE;

		this.win.add(okButton, c);
		this.win.getRootPane().setDefaultButton(okButton);
		try {
			this.centerWindow();
		} catch (Exception e) {
			System.out.println("Error, window could not be centered!");
		}
		this.win.setVisible( true );
	}
	
	/**
	 * <p>
	 * Constructs a new SGWindow instance and launches the Schedule Generation
	 * program with "input.txt" as the input filename and "newInput.txt"
	 * as the filtered input filename.
	 * </p>
	 *
	 * @throws Exception		FIXME: If the input could not be read, JFrame could not be constructed, or if JFrame.EXIT_ON_CLOSE causes a SecurityException
	 */
	public SGWindow() throws Exception {
		this("input.txt", "newInput.txt");
	}

	/**
	 * Centers JFrame in window, if the Toolkit is "obtainable".
	 *
	 * @throws Exception	If the Toolkit could not be obtained
	 * @see    Toolkit
	 */
	public void centerWindow() throws Exception {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if(this.win != null) {
				int centerX = (int)(screenSize.getWidth() - this.win.getWidth()) / 2;
				int centerY = (int)(screenSize.getHeight() - this.win.getHeight()) / 2;
				this.win.setLocation(centerX, centerY);
			}
		} catch(AWTError e) {
			System.err.format("%s%n", e);
			throw new Exception("centerWindow failed", e);
		}
	}

	/**
	 * <p>
	 * Compiles a list of SGCourseTime objects into a list of SGTimeRange
	 * objects based on the time ranges in the course times.
	 *
	 * The output ArrayList of SGTimeRange objects is sorted using the
	 * {@link SGTimeRange#mergeSortTimeRangeArrayList} method. The input
	 * ArrayList of SGCourseTime objects is assumed to be sorted in ascending
	 * order.
	 * </p>
	 * <p>
	 * For each SGCourseTime object, the time range is compared to those already
	 * added to the compiled list. If the time ranges are the same, the days
	 * used for both time ranges are logically OR'd and saved in the compiled
	 * list. If the time ranges are different, the time range is added to the
	 * compiled list, as is.
	 * </p>
	 * <p>
	 * After the output list is compiled, is is sorted using the aforementioned
	 * method, and then it removes any overlapping time ranges, keeping the
	 * first instance of the time range in the list (making it the earliest and
	 * shortest of those that are overlapping).
	 * </p>
	 *
	 * @param courseTimes	the ArrayList of SGCourseTime objects for all coursees
	 * @return				the ArrayList of compiled SGTimeRange objects
	 */
	private ArrayList<SGTimeRange> getDayTimes(ArrayList<SGCourseTime> courseTimes) {
		// Read in courseTimes to SGTimeRange Objects, ignoring duplicate days and overlapping time ranges
		ArrayList<SGTimeRange> timeRanges = new ArrayList<SGTimeRange>();
		if(courseTimes != null) {
			for(int i = 0; i < courseTimes.size(); ++i) {
				SGCourseTime         currCourse = courseTimes.get(i);
				SGTimeRange currRange = currCourse.timePeriod;
				String            currDays  = currRange.getDays();
				boolean found   = false;
				for(int j = 0; j < timeRanges.size(); ++j) {
					if(SGTimeRange.compareTimeRangeStarts(currRange, timeRanges.get(j)) == 0 && SGTimeRange.compareTimeRangeEnds(currRange, timeRanges.get(j)) == 0) {
						found = true;
						// Logically OR the days
						for(int k = 0; k < SGTimeRange.weekdays.length(); ++k) {
							timeRanges.get(j).daysUsed[k] = currRange.daysUsed[k] || timeRanges.get(j).daysUsed[k];
						}
					}
				}
				if(!found) {
					timeRanges.add(new SGTimeRange(currRange));
				}
			}
			SGTimeRange.mergeSortTimeRangeArrayList(timeRanges, 0, timeRanges.size());
		}
		// Remove Overlaps, preferring shorter time periods
		for(int i = 0; i < SGTimeRange.weekdays.length(); ++i) {
			for(int j = 0; j < timeRanges.size(); ++j) {
				if(timeRanges.get(j).daysUsed[i]) {
					SGTimeRange currRange = timeRanges.get(j);
					for(int k = j + 1; k < timeRanges.size(); ++k) {
						if(timeRanges.get(k).daysUsed[i]) {
							SGTimeRange compRange = timeRanges.get(k);
							if(currRange.overlapsRange(compRange)) {
								timeRanges.remove(k);
								--k;
							}
							else if(SGTimeRange.compareTimeRangeStarts(currRange, compRange) < 0) {
								break;
							}
						}
					}
				}
			}
		}
		return timeRanges;
	}

	/**
	 * <p>
	 * Creates an ArrayList of SGCourseTime objects from an ArrayList of lines
	 * from a formatted input file.
	 *
	 * The output ArrayList of SGCourseTime objects is sorted using the
	 * {@link SGCourseTime#mergeSortSGCourseTimeArrayList}.
	 * </p>
	 * <p>
	 * The file is assumed to be formatted where all available time period
	 * options for a given course are together, with no extra spacing lines,
	 * listed underneath the course name to be used.
	 * </p>
	 * <p>
	 * The course name lines are to begin with a numeric character and the
	 * first tab on the line should be the character directly before the course
	 * name.
	 * </p>
	 * <p>
	 * The time period option lines are to have a string of single letter days,
	 * with no spaces. The string of days should be followed by a time range,
	 * in the necessary format for either 12- or 24-hour format for
	 * SGTimeRange, with a tab between them.
	 * </p>
	 * <p>
	 * All other lines <i>should</i> be empty, but alphabetic characters that
	 * are not day abbreviations and non-alphanumeric characters can start
	 * lines that are to be ignored.
	 * </p>
	 *
	 * @param lines		the ArrayList of lines from the input file
	 */
	private void getSGCourseTimes(ArrayList<String> lines) {
		// Read in lines to SGCourseTime Objects
		this.courseTimes = new ArrayList<SGCourseTime>();
		String courseName = "";
		if(lines != null) {
			for(int i = 0; i < lines.size(); ++i) {
				String currLine  = lines.get(i);
				if(currLine.trim().length() > 0 && SGTimeRange.weekdays.indexOf(currLine.charAt(0)) != -1) {
					String courseDays   = currLine.substring(0, currLine.indexOf('\t'));
					String rangeString = currLine.substring(currLine.indexOf('\t') + 1);
					this.courseTimes.add(new SGCourseTime(rangeString, courseDays, courseName));
				}
				else if(currLine.trim().length() > 0) {
					courseName = currLine.substring(currLine.indexOf('\t') + 1);
				}
				else {
					courseName = "";
				}
			}
			SGCourseTime.mergeSortSGCourseTimeArrayList(this.courseTimes, 0, this.courseTimes.size());
		}
	}

	/**
	 * <p>
	 * Determines and outputs the days used by a list of SGTimeRange
	 * objects in a String of single-letter days.
	 *
	 * The string output will copy its letters from the
	 * {@link SGTimeRange#weekdays} static field.
	 * </p>
	 * <p>
	 * This method assumes that the daysUsed boolean array stored in
	 * SGTimeRange objects will be no greater than 7 items long.
	 * </p>
	 *
	 * @param timeRanges	the ArrayList of SGTimeRange objects
	 */
	private void determineDaysUsed(ArrayList<SGTimeRange> timeRanges) {
		boolean daysUsed[] = {false, false, false, false, false, false, false};
		this.days = "";
		if(timeRanges != null) {
			for(int i = 0; i < timeRanges.size(); ++i) {
				SGTimeRange currRange = timeRanges.get(i);
				for(int j = 0; j < SGTimeRange.weekdays.length(); ++j) {
					daysUsed[j] = daysUsed[j] || currRange.daysUsed[j];
				}
			}
			for(int i = 0; i < SGTimeRange.weekdays.length(); ++i) {
				if(daysUsed[i]) {
					this.days = this.days.concat(SGTimeRange.weekdays.substring(i, i + 1));
				}
			}
		}
	}

	/**
	 * <p>
	 * Determines the negative preferences given by the SGCheckBoxGrid.
	 * </p>
	 * <p>
	 * "Negative preferences" are defined as the time ranges outlined by
	 * unselected boxes. For each box that is left unchecked, a SGTimeRange
	 * object is generated and added to the list. This method makes preference
	 * checking more efficient and simple by only needing to check for an
	 * overlap with this "negative preferences" to determine if the time range
	 * would be preferred.
	 * </p>
	 *
	 * @return	the ArrayList of negative preferences
	 */
	private ArrayList<ArrayList<SGTimeRange>> getNegPreferences() {
		ArrayList<ArrayList<SGTimeRange>> negPrefDayRanges = new ArrayList<ArrayList<SGTimeRange>>();
		for(int i = 0; i < this.checkBoxes.boxGrid.size(); ++i) {
			ArrayList<SGTimeRange> currRanges = new ArrayList<SGTimeRange>();
			for(int j = 0; j < this.checkBoxes.boxGrid.get(i).size(); ++j) {
				if(!(this.checkBoxes.boxGrid.get(i).get(j).isSelected())) {
					currRanges.add(new SGTimeRange(this.checkBoxes.boxGrid.get(i).get(j).getText(), this.days.substring(i, i + 1)));
				}
			}
			negPrefDayRanges.add(currRanges);
		}
		return negPrefDayRanges;
	}

	/**
	 * Determines all of the course names from the SGCourseTime objects.
	 *
	 * @return	the ArrayList of course names
	 */
	private ArrayList<String> getCourseNames() {
		ArrayList<String> courseNames = new ArrayList<String>();
		for(int i = 0; i < this.courseTimes.size(); ++i) {
			if(!courseNames.contains(this.courseTimes.get(i).courseName)) {
				courseNames.add(this.courseTimes.get(i).courseName);
			}
		}
		return courseNames;
	}

	/**
	 * <p>
	 * Determines negative preferences from SGCheckBoxGrid and generates a new
	 * input file taking these preferences into account.
	 * </p>
	 * <p>
	 * This method continues the process by creating a new input file for
	 * the {@link ScheduleGenerator} class to use, based on the preferences
	 * given. All unchecked boxes are gathered as "off-limits" time ranges to
	 * start.
	 * </p>
	 * <p>
	 * Then the new input file writing begins, writing a number to start
	 * the line and then finishing with a tab followed by the course name (to
	 * follow the format shown above in the {@link getSGCourseTimes} method).
	 * After writing the course name, the first SGCourseTime for this course is found
	 * and its time range is compared to the "off-limits" ranges. If they overlap
	 * in any way, the time range is skipped.
	 * </p>
	 * <p>
	 * This process repeats until all SGCourseTime instances have been found for
	 * the respective course. If all were skipped, it overrides the preferences
	 * and writes all options for <i>that course only</i>. Finally, it proceeds
	 * to the next course and so on until all courses have been written to the
	 * new input file.
	 * </p>
	 * <p>
	 * Once this new file has been fully generated, the preference window closes
	 * and the {@link ScheduleGenerator#generateSchedule} method is called to
	 * finish the process.
	 * </p>
	 *
	 * @param e		the ActionEvent that invoked the method
	 */
	public void actionPerformed( ActionEvent e ) {
		// Make window invisible
		this.win.setVisible( false );
		// Generate negative preference list
		ArrayList<ArrayList<SGTimeRange>> negPrefDayRanges = getNegPreferences();
		// Generate new input file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("preferredInput.txt"));
			// Get course names to use for output file
			ArrayList<String> courseNames = getCourseNames();

			for(int i = 0; i < courseNames.size(); ++i) {
				boolean coursePreferred = false;
				writer.write(String.format("%d)\t%s", i + 1, courseNames.get(i)));
				writer.newLine();
				writer.flush();
				for(int j = SGCourseTime.searchForCourseInArrayList(this.courseTimes, courseNames.get(i)); j < this.courseTimes.size() && j >= 0; j = SGCourseTime.searchForCourseInArrayList(this.courseTimes, courseNames.get(i), ++j)) {
					boolean timePreferred = true;
					SGTimeRange currRange = this.courseTimes.get(j).timePeriod;
					String            currDays  = currRange.getDays();
					for(int k = 0; k < currDays.length() && timePreferred; ++k) {
						ArrayList<SGTimeRange> negPrefRanges = negPrefDayRanges.get(this.days.indexOf(currDays.charAt(k)));
						for(int l = 0; l < negPrefRanges.size(); ++l) {
							timePreferred = timePreferred && !currRange.overlapsRange(negPrefRanges.get(l));
						}
					}
					if(timePreferred) {
						coursePreferred = true;
						writer.write(String.format("%s\t%s", currDays, currRange.rangeString()));
						writer.newLine();
						writer.flush();
					}
				}
				if(!coursePreferred) {
					// EVENTUALLY PROMPT FOR METHOD OF PROCEEDING; FOR NOW, JUST INPUT ALL COURSE TIMES DESPITE CONFLICT
					for(int j = SGCourseTime.searchForCourseInArrayList(this.courseTimes, courseNames.get(i)); j < this.courseTimes.size() && j >= 0; j = SGCourseTime.searchForCourseInArrayList(this.courseTimes, courseNames.get(i), ++j)) {
						SGTimeRange currRange = this.courseTimes.get(j).timePeriod;
						String            currDays  = currRange.getDays();
						writer.write(String.format("%s\t%s", currDays, currRange.rangeString()));
						writer.newLine();
						writer.flush();
					}
				}
				writer.newLine();
				writer.flush();
			}
		} catch (IOException ioe) {
			System.err.format("%s%n", ioe);
			// Flash to convey error
			this.win.setVisible( true );
		}
		// Close Window when finished with preferences
		this.win.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
		this.win.dispatchEvent(new WindowEvent(this.win, WindowEvent.WINDOW_CLOSING));
		// Generate Schedules
		try {
			ScheduleGenerator.generateSchedule(this.newFilename);
		} catch(Exception ex) {
			System.out.println("Error, schedules unable to be generated!");
		}
	}
}