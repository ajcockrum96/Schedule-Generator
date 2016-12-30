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
	 * Compiled list of class time period options
	 */
	private ArrayList<ClassTime> classTimes;

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
	 * filename as lines. Then a list of ClassTime objects is obtained from the
	 * lines and a compiled list of ScheduleTimeRange objects is obtained from
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
		
		// Get Times from Input Lines as Classes
		getClassTimes(lines);

		// Get Times from Input Classes as Compiled List
		ArrayList<ScheduleTimeRange> timeRanges = getDayTimes(this.classTimes);
		int dayLength = ScheduleTimeRange.compareTimeRangeStarts(timeRanges.get(timeRanges.size() - 1), timeRanges.get(0)) / 60;
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
	 * Compiles a list of ClassTime objects into a list of ScheduleTimeRange
	 * objects based on the time ranges in the class times.
	 *
	 * The output ArrayList of ScheduleTimeRange objects is sorted using the
	 * {@link ScheduleTimeRange#mergeSortTimeRangeArrayList} method. The input
	 * ArrayList of ClassTime objects is assumed to be sorted in ascending
	 * order.
	 * </p>
	 * <p>
	 * For each ClassTime object, the time range is compared to those already
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
	 * @param classTimes	the ArrayList of ClassTime objects for all classes
	 * @return				the ArrayList of compiled ScheduleTimeRange objects
	 */
	private ArrayList<ScheduleTimeRange> getDayTimes(ArrayList<ClassTime> classTimes) {
		// Read in classTimes to ScheduleTimeRange Objects, ignoring duplicate days and overlapping time ranges
		ArrayList<ScheduleTimeRange> timeRanges = new ArrayList<ScheduleTimeRange>();
		if(classTimes != null) {
			for(int i = 0; i < classTimes.size(); ++i) {
				ClassTime         currClass = classTimes.get(i);
				ScheduleTimeRange currRange = currClass.timePeriod;
				String            currDays  = currRange.getDays();
				boolean found   = false;
				for(int j = 0; j < timeRanges.size(); ++j) {
					if(ScheduleTimeRange.compareTimeRangeStarts(currRange, timeRanges.get(j)) == 0 && ScheduleTimeRange.compareTimeRangeEnds(currRange, timeRanges.get(j)) == 0) {
						found = true;
						// Logically OR the days
						for(int k = 0; k < ScheduleTimeRange.weekdays.length(); ++k) {
							timeRanges.get(j).daysUsed[k] = currRange.daysUsed[k] || timeRanges.get(j).daysUsed[k];
						}
					}
				}
				if(!found) {
					timeRanges.add(new ScheduleTimeRange(currRange));
				}
			}
			ScheduleTimeRange.mergeSortTimeRangeArrayList(timeRanges, 0, timeRanges.size());
		}
		// Remove Overlaps, preferring shorter time periods
		for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
			for(int j = 0; j < timeRanges.size(); ++j) {
				if(timeRanges.get(j).daysUsed[i]) {
					ScheduleTimeRange currRange = timeRanges.get(j);
					for(int k = j + 1; k < timeRanges.size(); ++k) {
						if(timeRanges.get(k).daysUsed[i]) {
							ScheduleTimeRange compRange = timeRanges.get(k);
							if(currRange.overlapsRange(compRange)) {
								timeRanges.remove(k);
								--k;
							}
							else if(ScheduleTimeRange.compareTimeRangeStarts(currRange, compRange) < 0) {
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
	 * Creates an ArrayList of ClassTime objects from an ArrayList of lines
	 * from a formatted input file.
	 *
	 * The output ArrayList of ClassTime objects is sorted using the
	 * {@link ClassTime#mergeSortClassTimeArrayList}.
	 * </p>
	 * <p>
	 * The file is assumed to be formatted where all available time period
	 * options for a given class are together, with no extra spacing lines,
	 * listed underneath the class name to be used.
	 * </p>
	 * <p>
	 * The class name lines are to begin with a numeric character and the
	 * first tab on the line should be the character directly before the class
	 * name.
	 * </p>
	 * <p>
	 * The time period option lines are to have a string of single letter days,
	 * with no spaces. The string of days should be followed by a time range,
	 * in the necessary format for either 12- or 24-hour format for
	 * ScheduleTimeRange, with a tab between them.
	 * </p>
	 * <p>
	 * All other lines <i>should</i> be empty, but alphabetic characters that
	 * are not day abbreviations and non-alphanumeric characters can start
	 * lines that are to be ignored.
	 * </p>
	 *
	 * @param lines		the ArrayList of lines from the input file
	 */
	private void getClassTimes(ArrayList<String> lines) {
		// Read in lines to ClassTime Objects
		this.classTimes = new ArrayList<ClassTime>();
		String className = "";
		if(lines != null) {
			for(int i = 0; i < lines.size(); ++i) {
				String currLine  = lines.get(i);
				if(currLine.trim().length() > 0 && ScheduleTimeRange.weekdays.indexOf(currLine.charAt(0)) != -1) {
					String classDays   = currLine.substring(0, currLine.indexOf('\t'));
					String rangeString = currLine.substring(currLine.indexOf('\t') + 1);
					this.classTimes.add(new ClassTime(rangeString, classDays, className));
				}
				else if(currLine.trim().length() > 0) {
					className = currLine.substring(currLine.indexOf('\t') + 1);
				}
				else {
					className = "";
				}
			}
			ClassTime.mergeSortClassTimeArrayList(this.classTimes, 0, this.classTimes.size());
		}
	}

	/**
	 * <p>
	 * Determines and outputs the days used by a list of ScheduleTimeRange
	 * objects in a String of single-letter days.
	 *
	 * The string output will copy its letters from the
	 * {@link ScheduleTimeRange#weekdays} static field.
	 * </p>
	 * <p>
	 * This method assumes that the daysUsed boolean array stored in
	 * ScheduleTimeRange objects will be no greater than 7 items long.
	 * </p>
	 *
	 * @param timeRanges	the ArrayList of ScheduleTimeRange objects
	 */
	private void determineDaysUsed(ArrayList<ScheduleTimeRange> timeRanges) {
		boolean daysUsed[] = {false, false, false, false, false, false, false};
		this.days = "";
		if(timeRanges != null) {
			for(int i = 0; i < timeRanges.size(); ++i) {
				ScheduleTimeRange currRange = timeRanges.get(i);
				for(int j = 0; j < ScheduleTimeRange.weekdays.length(); ++j) {
					daysUsed[j] = daysUsed[j] || currRange.daysUsed[j];
				}
			}
			for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
				if(daysUsed[i]) {
					this.days = this.days.concat(ScheduleTimeRange.weekdays.substring(i, i + 1));
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
	 * unselected boxes. For each box that is left unchecked, a ScheduleTimeRange
	 * object is generated and added to the list. This method makes preference
	 * checking more efficient and simple by only needing to check for an
	 * overlap with this "negative preferences" to determine if the time range
	 * would be preferred.
	 * </p>
	 *
	 * @return	the ArrayList of negative preferences
	 */
	private ArrayList<ArrayList<ScheduleTimeRange>> getNegPreferences() {
		ArrayList<ArrayList<ScheduleTimeRange>> negPrefDayRanges = new ArrayList<ArrayList<ScheduleTimeRange>>();
		for(int i = 0; i < this.checkBoxes.boxGrid.size(); ++i) {
			ArrayList<ScheduleTimeRange> currRanges = new ArrayList<ScheduleTimeRange>();
			for(int j = 0; j < this.checkBoxes.boxGrid.get(i).size(); ++j) {
				if(!(this.checkBoxes.boxGrid.get(i).get(j).isSelected())) {
					currRanges.add(new ScheduleTimeRange(this.checkBoxes.boxGrid.get(i).get(j).getText(), this.days.substring(i, i + 1)));
				}
			}
			negPrefDayRanges.add(currRanges);
		}
		return negPrefDayRanges;
	}

	/**
	 * Determines all of the class names from the ClassTime objects.
	 *
	 * @return	the ArrayList of class names
	 */
	private ArrayList<String> getClassNames() {
		ArrayList<String> classNames = new ArrayList<String>();
		for(int i = 0; i < this.classTimes.size(); ++i) {
			if(!classNames.contains(this.classTimes.get(i).className)) {
				classNames.add(this.classTimes.get(i).className);
			}
		}
		return classNames;
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
	 * the line and then finishing with a tab followed by the class name (to
	 * follow the format shown above in the {@link getClassTimes} method).
	 * After writing the class name, the first ClassTime for this class is found
	 * and its time range is compared to the "off-limits" ranges. If they overlap
	 * in any way, the time range is skipped.
	 * </p>
	 * <p>
	 * This process repeats until all ClassTime instances have been found for
	 * the respective class. If all were skipped, it overrides the preferences
	 * and writes all options for <i>that class only</i>. Finally, it proceeds
	 * to the next class and so on until all classes have been written to the
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
		ArrayList<ArrayList<ScheduleTimeRange>> negPrefDayRanges = getNegPreferences();
		// Generate new input file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("preferredInput.txt"));
			// Get class names to use for output file
			ArrayList<String> classNames = getClassNames();

			for(int i = 0; i < classNames.size(); ++i) {
				boolean classPreferred = false;
				writer.write(String.format("%d)\t%s", i + 1, classNames.get(i)));
				writer.newLine();
				writer.flush();
				for(int j = ClassTime.searchForClassInArrayList(this.classTimes, classNames.get(i)); j < this.classTimes.size() && j >= 0; j = ClassTime.searchForClassInArrayList(this.classTimes, classNames.get(i), ++j)) {
					boolean timePreferred = true;
					ScheduleTimeRange currRange = this.classTimes.get(j).timePeriod;
					String            currDays  = currRange.getDays();
					for(int k = 0; k < currDays.length() && timePreferred; ++k) {
						ArrayList<ScheduleTimeRange> negPrefRanges = negPrefDayRanges.get(this.days.indexOf(currDays.charAt(k)));
						for(int l = 0; l < negPrefRanges.size(); ++l) {
							timePreferred = timePreferred && !currRange.overlapsRange(negPrefRanges.get(l));
						}
					}
					if(timePreferred) {
						classPreferred = true;
						writer.write(String.format("%s\t%s", currDays, currRange.rangeString()));
						writer.newLine();
						writer.flush();
					}
				}
				if(!classPreferred) {
					// EVENTUALLY PROMPT FOR METHOD OF PROCEEDING; FOR NOW, JUST INPUT ALL CLASS TIMES DESPITE CONFLICT
					for(int j = ClassTime.searchForClassInArrayList(this.classTimes, classNames.get(i)); j < this.classTimes.size() && j >= 0; j = ClassTime.searchForClassInArrayList(this.classTimes, classNames.get(i), ++j)) {
						ScheduleTimeRange currRange = this.classTimes.get(j).timePeriod;
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