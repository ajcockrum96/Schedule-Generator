import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class SGWindow {
	String weekdays = "SMTWRFA";
	public SGWindow(String filename) throws Exception {
		// Read File Lines
		ArrayList<String> lines;
		try {
			lines = readFileLines(filename);
		} catch (Exception e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		}
		
		ArrayList<ArrayList<String>> Times = SGWindow.getDayTimes(lines, weekdays);
		ArrayList<ArrayList<ScheduleTimeRange>> timeRanges = new ArrayList<ArrayList<ScheduleTimeRange>>();
		
		for(int i = 0; i < Times.size(); ++i) {
			ArrayList<ScheduleTimeRange> dayRanges = new ArrayList<ScheduleTimeRange>();
			for(int j = 0; j < Times.get(i).size() - 1; ++j) {
				ScheduleTimeRange range = new ScheduleTimeRange(Times.get(i).get(j));
				dayRanges.add(range);
			}
			timeRanges.add(dayRanges);
		}

		ArrayList<String> compiledTimes = SGWindow.compileTimes(Times);
		ScheduleTimeRange dayStartRange = new ScheduleTimeRange(compiledTimes.get(0).substring(0, compiledTimes.get(0).indexOf('-')).concat(compiledTimes.get(compiledTimes.size() - 1).substring(compiledTimes.get(compiledTimes.size() - 1).indexOf('-'))));
		int dayLength = (int)dayStartRange.getHourLength();
		
		ArrayList<ArrayList<JCheckBox>> checkBoxes = new ArrayList<ArrayList<JCheckBox>>();
		for(int i = 0; i < Times.size(); ++i) {
			ArrayList<JCheckBox> dayBoxes = new ArrayList<JCheckBox>();
			for(int j = 0; j < Times.get(i).size() - 1; ++j) {
				JCheckBox hourBox = new JCheckBox(ScheduleTimeRange.convert24To12HourRange(timeRanges.get(i).get(j).rangeString()));
				dayBoxes.add(hourBox);
			}
			checkBoxes.add(dayBoxes);
		}

		// Initialize Window and Setup Check Boxes
		JFrame win = new JFrame("Schedule Generator");
		win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		win.setResizable( false );
		
		win.setSize( Times.size() * 150, dayLength * 75 );
		win.setLayout( new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();
		c.weightx    = 1;
		c.weighty    = 1;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.ipadx      = (int)win.getSize().getWidth() / (2 * Times.size());
		c.ipady      = (int)win.getSize().getHeight() / (2 * dayLength);
		c.fill       = GridBagConstraints.BOTH;
		
		for(int i = 0; i < checkBoxes.size(); ++i) {
			c.gridx = c.gridheight * i;
			c.gridy = 0;
			win.add(new JLabel(Times.get(i).get(Times.get(i).size() - 1)), c);
			for(int j = 0; j < checkBoxes.get(i).size() - 1; ++j) {
				int pos = ScheduleTimeRange.compareTimeRangeStarts(timeRanges.get(i).get(j), dayStartRange);
				// c.gridy = c.gridheight * j + 1;
				c.gridy = c.gridheight * pos + 1;
				win.add(checkBoxes.get(i).get(j), c);
			}
		}
		win.setVisible( true );
	}
	
	public SGWindow() throws Exception {
		this("input.txt");
	}
	
	static public ArrayList<String> readFileLines(String filename) throws Exception {
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
	
	static public ArrayList<ArrayList<String>> getDayTimes(ArrayList<String> lines, String days) {
		// Determine Days in Schedule File
		boolean daysPresent[] = {false, false, false, false, false, false, false};
		for(int i = 0; i < lines.size(); ++i) {
			if(lines.get(i).indexOf('\t') != -1) {
				String dayString = lines.get(i).substring(0, lines.get(i).indexOf('\t'));
				for(int j = 0; j < dayString.length(); ++j) {
					if(days.indexOf(dayString.charAt(j)) != -1) {
						daysPresent[days.indexOf(dayString.charAt(j))] = true;
					}
				}
			}
		}

		// Add Days to "Top" of 2D Array List
		ArrayList<ArrayList<String>> Times = new ArrayList<ArrayList<String>>();
			int k = 0;
		for(int i = 0; i < days.length(); ++i) {
			if(daysPresent[i]) {
				Times.add(new ArrayList<String>());
				Times.get(k).add(days.substring(i, i + 1));
				++k;
			}
		}

		for(int i = 0; i < Times.size(); ++i) {
			for(int j = 0; j < lines.size(); ++j) {
				if(lines.get(j).indexOf('\t') != -1) {
					String lineDays = lines.get(j).substring(0, lines.get(j).indexOf('\t'));
					if(lineDays.indexOf(Times.get(i).get(0)) != -1) {
						String time = lines.get(j).substring(lines.get(j).indexOf('\t') + 1);
						if(!Times.get(i).contains(time)) {
							Times.get(i).add(time);
						}
					}
				}
			}
			Collections.sort(Times.get(i));
		}
		return Times;
	}
	
	static public ArrayList<String> compileTimes(ArrayList<ArrayList<String>> times) {
		ArrayList<String> compiledTimes = new ArrayList<String>();
		for(int i = 0; i < times.size(); ++i) {
			for(int j = 0; j < times.get(i).size() - 1; ++j) {
				if(!compiledTimes.contains(times.get(i).get(j))) {
					compiledTimes.add(times.get(i).get(j));
				}
			}
		}
		return compiledTimes;
	}
}