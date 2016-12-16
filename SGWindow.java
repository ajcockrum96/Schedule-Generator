import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SGWindow implements ActionListener {
	String                          daysUsed = "";
	ArrayList<ArrayList<JCheckBox>> checkBoxes;
	ArrayList<ClassTime>            classTimes;

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

		checkBoxes = new ArrayList<ArrayList<JCheckBox>>();
		for(int i = 0; i < daysUsed.length(); ++i) {
			ArrayList<JCheckBox> dayBoxes = new ArrayList<JCheckBox>();
			for(int j = 0; j < timeRanges.size(); ++j) {
				ScheduleTimeRange currRange = timeRanges.get(j);
				if(currRange.getDays().indexOf(daysUsed.charAt(i)) != -1) {
					dayBoxes.add(new JCheckBox(ScheduleTimeRange.convert24To12HourRange(currRange.rangeString()), true));
				}
			}
			checkBoxes.add(dayBoxes);
		}

		// Initialize Window and Setup Check Boxes
		JFrame win = new JFrame("Schedule Generator");
		win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		win.setResizable( false );
		 
		win.setSize( checkBoxes.size() * 200, dayLength * 50 + 50 );
		win.setLayout( new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();
		c.weightx    = 1;
		c.weighty    = 1;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.ipadx      = (int)win.getSize().getWidth() / (2 * checkBoxes.size());
		c.ipady      = (int)win.getSize().getHeight() / (2 * dayLength);
		c.fill       = GridBagConstraints.BOTH;
		c.fill = GridBagConstraints.NONE;
	
		int k = 0;
		for(int i = 0; i < checkBoxes.size(); ++i, ++k) {
			c.gridx = c.gridheight * i;
			c.gridy = 0;
			win.add(new JLabel(daysUsed.substring(i, i + 1)));

			for(int j = 0; j < checkBoxes.get(i).size(); ++j) {
				ScheduleTimeRange currRange = new ScheduleTimeRange(checkBoxes.get(i).get(j).getText(), ScheduleTimeRange.weekdays.substring(k, k + 1));
				int pos = ScheduleTimeRange.compareTimeRangeStarts(currRange, timeRanges.get(0));
				c.gridy = c.gridheight * pos + 1;
				win.add(checkBoxes.get(i).get(j), c);
			}
		}
		c.gridwidth = checkBoxes.size();
		c.gridy += c.gridheight;
		c.gridx = 0;
		c.fill = GridBagConstraints.NONE;
		JButton okButton = new JButton("OK");
		okButton.addActionListener(this);
		win.add(okButton, c);
		win.setVisible( true );
	}
	
	public SGWindow() throws Exception {
		this("input.txt");
	}

	public ArrayList<ScheduleTimeRange> getDayTimes(ArrayList<ClassTime> classTimes) {
		// Read in lines to ScheduleTimeRange Objects, OR'ing duplicates for new days
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
		// Total up check boxes and generate new schedule
		ArrayList<ScheduleTimeRange> preferredDayRanges = new ArrayList<ScheduleTimeRange>();
		for(int i = 0; i < checkBoxes.size(); ++i) {
			String rangeString = "";
			// FIXME: Add checking if all checkboxes are NOT SELECTED
			// Get Day Start
			for(int j = 0; j < checkBoxes.get(i).size(); ++j) {
				if(checkBoxes.get(i).get(j).isSelected()) {
					String rangeStart = checkBoxes.get(i).get(j).getText();
					rangeString = rangeString.concat(rangeStart.substring(0, rangeStart.indexOf('-') + 1));
					break;
				}
			}
			// Get Day End
			for(int j = checkBoxes.get(i).size() - 1; j >= 0; --j) {
				if(checkBoxes.get(i).get(j).isSelected()) {
					String rangeEnd = checkBoxes.get(i).get(j).getText();
					rangeString = rangeString.concat(rangeEnd.substring(rangeEnd.indexOf('-') + 2));
					break;
				}
			}
			// Create Range Time
			preferredDayRanges.add(new ScheduleTimeRange(rangeString));
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
						ScheduleTimeRange preferredRange = preferredDayRanges.get(daysUsed.indexOf(currDays.charAt(k)));
						timePreferred = preferredRange.containsRange(currRange);
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
		}
	}
}