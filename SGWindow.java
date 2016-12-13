import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class SGWindow {
	static String weekdays = "SMTWRFA";
	boolean daysUsed[] = {false, false, false, false, false, false, false};
	public SGWindow(String filename) throws Exception {
		// Read File Lines
		ArrayList<String> lines;
		try {
			lines = ScheduleGenerator.readInputFile(filename);
		} catch (Exception e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		}
		
		ArrayList<ScheduleTimeRange> timeRanges = getDayTimes(lines);
		int dayLength = ScheduleTimeRange.compareTimeRangeStarts(timeRanges.get(timeRanges.size() - 1), timeRanges.get(0));
		determineDaysUsed(timeRanges);

		ArrayList<ArrayList<JCheckBox>> checkBoxes = new ArrayList<ArrayList<JCheckBox>>();
		for(int i = 0; i < weekdays.length(); ++i) {
			if(daysUsed[i]) {
				ArrayList<JCheckBox> dayBoxes = new ArrayList<JCheckBox>();
				for(int j = 0; j < timeRanges.size(); ++j) {
					ScheduleTimeRange currRange = timeRanges.get(j);
					if(currRange.daysUsed[i]) {
						dayBoxes.add(new JCheckBox(ScheduleTimeRange.convert24To12HourRange(currRange.rangeString()), true));
					}
				}
				checkBoxes.add(dayBoxes);
			}
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
			while(!daysUsed[k]) {
				++k;
			}
			win.add(new JLabel(weekdays.substring(k, k + 1)));

			for(int j = 0; j < checkBoxes.get(i).size(); ++j) {
				ScheduleTimeRange currRange = new ScheduleTimeRange(checkBoxes.get(i).get(j).getText(), weekdays.substring(k, k + 1));
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
		win.add(okButton, c);
		win.setVisible( true );
	}
	
	public SGWindow() throws Exception {
		this("input.txt");
	}
	
	
	public ArrayList<ScheduleTimeRange> getDayTimes(ArrayList<String> lines) {
		// Read in lines to ScheduleTimeRange Objects, OR'ing duplicates for new days
		ArrayList<ScheduleTimeRange> timeRanges = new ArrayList<ScheduleTimeRange>();
		for(int i = 0; i < lines.size(); ++i) {
			String currLine = lines.get(i);
			if(lines.get(i).length() > 0) {
				char firstChar = currLine.charAt(0);
				if(weekdays.indexOf(firstChar) != -1) {
					String daysUsed   = currLine.substring(0, currLine.indexOf('\t'));
					String rangeString = currLine.substring(currLine.indexOf('\t') + 1);
					ScheduleTimeRange timeRange = new ScheduleTimeRange(rangeString, daysUsed);
					boolean found = false;
					for(int j = 0; j < timeRanges.size(); ++j) {
						if(ScheduleTimeRange.compareTimeRangeStarts(timeRange, timeRanges.get(j)) == 0 && ScheduleTimeRange.compareTimeRangeEnds(timeRange, timeRanges.get(j)) == 0) {
							found = true;
							// Logically OR the days
							for(int k = 0; k < weekdays.length(); ++k) {
								timeRanges.get(j).daysUsed[k] = timeRange.daysUsed[k] || timeRanges.get(j).daysUsed[k];
							}
						}
					}
					if(!found) {
						timeRanges.add(timeRange);
					}
				}
			}
		}
		ScheduleTimeRange.sortTimeRangeArrayList(timeRanges);
		return timeRanges;
	}

	public void determineDaysUsed(ArrayList<ScheduleTimeRange> timeRanges) {
		for(int i = 0; i < timeRanges.size(); ++i) {
			ScheduleTimeRange currRange = timeRanges.get(i);
			for(int j = 0; j < weekdays.length(); ++j) {
				daysUsed[j] = daysUsed[j] || currRange.daysUsed[j];
			}
		}
	}
}