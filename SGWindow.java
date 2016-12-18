import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SGWindow implements ActionListener {
	String               daysUsed = "";
	SGCheckBoxGrid       checkBoxes;
	ArrayList<ClassTime> classTimes;
	JFrame               win;

	public SGWindow(String filename) throws Exception {
		// Read File Lines
		ArrayList<String> lines;
		try {
			lines = ScheduleGenerator.readInputFile(filename);
		} catch (Exception e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		}
		
		// Get Times from Input Lines as Classes
		classTimes = getClassTimes(lines);

		// Get Times from Input Classes as Compiled List
		ArrayList<ScheduleTimeRange> timeRanges = getDayTimes(classTimes);
		int dayLength = ScheduleTimeRange.compareTimeRangeStarts(timeRanges.get(timeRanges.size() - 1), timeRanges.get(0));
		daysUsed = determineDaysUsed(timeRanges);

		// Set up check boxes
		checkBoxes = new SGCheckBoxGrid(timeRanges, daysUsed);

		// Initialize Window
		win = new JFrame("Schedule Generator");
		win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		win.setResizable( false );
		 
		win.setSize( daysUsed.length() * 200, checkBoxes.dayRange * 50 + 50 );
		win.setLayout( new GridBagLayout() );

		GridBagConstraints c = new GridBagConstraints();
		c.gridx      = 0;
		c.gridy      = 0;
		c.gridwidth  = daysUsed.length();
		c.gridheight = checkBoxes.dayRange;
		c.weightx    = 1;
		c.weighty    = 1;
		c.ipadx      = (int)(win.getSize().getWidth()) / (2 * daysUsed.length());
		c.ipady      = (int)(win.getSize().getHeight()) / (2 * checkBoxes.dayRange);
		c.fill       = GridBagConstraints.BOTH;
		win.add(checkBoxes.fullGrid, c);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		c.gridx      = 0;
		c.gridy      = c.gridheight;
		c.gridwidth  = daysUsed.length();
		c.gridheight = 1;
		c.weightx    = 0;
		c.weighty    = 0;
		c.fill       = GridBagConstraints.NONE;

		win.add(okButton, c);
		win.getRootPane().setDefaultButton(okButton);
		win.setVisible( true );
	}
	
	public SGWindow() throws Exception {
		this("input.txt");
	}

	public ArrayList<ScheduleTimeRange> getDayTimes(ArrayList<ClassTime> classTimes) {
		// Read in classTimes to ScheduleTimeRange Objects, ignoring duplicate days and overlapping time ranges
		ArrayList<ScheduleTimeRange> timeRanges = new ArrayList<ScheduleTimeRange>();
		for(int i = 0; i < classTimes.size(); ++i) {
			ClassTime         currClass = classTimes.get(i);
			ScheduleTimeRange currRange = currClass.timePeriod;
			String            currDays  = currRange.getDays();
			boolean found   = false;
			boolean overlap = false;
			for(int j = 0; j < timeRanges.size(); ++j) {
				overlap = overlap || currRange.overlapsRange(timeRanges.get(j));
				if(ScheduleTimeRange.compareTimeRangeStarts(currRange, timeRanges.get(j)) == 0 && ScheduleTimeRange.compareTimeRangeEnds(currRange, timeRanges.get(j)) == 0) {
					found = true;
					// Logically OR the days
					for(int k = 0; k < ScheduleTimeRange.weekdays.length(); ++k) {
						timeRanges.get(j).daysUsed[k] = currRange.daysUsed[k] || timeRanges.get(j).daysUsed[k];
					}
				}
			}
			if(!found && !overlap) {
				timeRanges.add(new ScheduleTimeRange(currRange));
			}
		}
		ScheduleTimeRange.mergeSortTimeRangeArrayList(timeRanges, 0, timeRanges.size());
		return timeRanges;
	}

	public ArrayList<ClassTime> getClassTimes(ArrayList<String> lines) {
		// Read in lines to ClassTime Objects
		ArrayList<ClassTime> classTimes = new ArrayList<ClassTime>();
		String className = "";
		for(int i = 0; i < lines.size(); ++i) {
			String currLine  = lines.get(i);
			if(currLine.trim().length() > 0 && ScheduleTimeRange.weekdays.indexOf(currLine.charAt(0)) != -1) {
				String classDays   = currLine.substring(0, currLine.indexOf('\t'));
				String rangeString = currLine.substring(currLine.indexOf('\t') + 1);
				classTimes.add(new ClassTime(rangeString, classDays, className));
			}
			else if(currLine.trim().length() > 0) {
				className = currLine.substring(currLine.indexOf('\t') + 1);
			}
			else {
				className = "";
			}
		}
		ClassTime.mergeSortClassTimeArrayList(classTimes, 0, classTimes.size());
		return classTimes;
	}

	public String determineDaysUsed(ArrayList<ScheduleTimeRange> timeRanges) {
		boolean daysUsed[] = {false, false, false, false, false, false, false};
		for(int i = 0; i < timeRanges.size(); ++i) {
			ScheduleTimeRange currRange = timeRanges.get(i);
			for(int j = 0; j < ScheduleTimeRange.weekdays.length(); ++j) {
				daysUsed[j] = daysUsed[j] || currRange.daysUsed[j];
			}
		}
		String days = "";
		for(int i = 0; i < ScheduleTimeRange.weekdays.length(); ++i) {
			if(daysUsed[i]) {
				days = days.concat(ScheduleTimeRange.weekdays.substring(i, i + 1));
			}
		}
		return days;
	}

	public void actionPerformed( ActionEvent e ) {
		// Make window invisible
		win.setVisible( false );
		// Total up check boxes and generate new schedule
		ArrayList<ArrayList<ScheduleTimeRange>> negPrefDayRanges = new ArrayList<ArrayList<ScheduleTimeRange>>();
		for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
			ArrayList<ScheduleTimeRange> currRanges = new ArrayList<ScheduleTimeRange>();
			for(int j = 0; j < checkBoxes.boxGrid.get(i).size(); ++j) {
				if(!(checkBoxes.boxGrid.get(i).get(j).isSelected())) {
					currRanges.add(new ScheduleTimeRange(checkBoxes.boxGrid.get(i).get(j).getText(), daysUsed.substring(i, i + 1)));
				}
			}
			negPrefDayRanges.add(currRanges);
		}
		// Generate new input file
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("preferredInput.txt"));
			// FIXING FOR USE OF CLASS TIME
			// Get class names
			ArrayList<String> classNames = new ArrayList<String>();
			for(int i = 0; i < classTimes.size(); ++i) {
				if(!classNames.contains(classTimes.get(i).className)) {
					classNames.add(classTimes.get(i).className);
				}
			}
			for(int i = 0; i < classNames.size(); ++i) {
				boolean classPreferred = false;
				writer.write(String.format("%d)\t%s", i + 1, classNames.get(i)));
				writer.newLine();
				writer.flush();
				for(int j = ClassTime.searchForClassInArrayList(classTimes, classNames.get(i)); j < classTimes.size() && j >= 0; j = ClassTime.searchForClassInArrayList(classTimes, classNames.get(i), ++j)) {
					boolean timePreferred = true;
					ScheduleTimeRange currRange = classTimes.get(j).timePeriod;
					String            currDays  = currRange.getDays();
					for(int k = 0; k < currDays.length() && timePreferred; ++k) {
						ArrayList<ScheduleTimeRange> negPrefRanges = negPrefDayRanges.get(daysUsed.indexOf(currDays.charAt(k)));
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
					for(int j = ClassTime.searchForClassInArrayList(classTimes, classNames.get(i)); j < classTimes.size() && j >= 0; j = ClassTime.searchForClassInArrayList(classTimes, classNames.get(i), ++j)) {
						ScheduleTimeRange currRange = classTimes.get(j).timePeriod;
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
			win.setVisible( true );
		}
		// Close Window when finished
		win.dispatchEvent(new WindowEvent(win, WindowEvent.WINDOW_CLOSING));
	}
}