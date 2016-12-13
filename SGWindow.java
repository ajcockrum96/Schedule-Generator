import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;

public class SGWindow {
	char weekdays[] = ("SMTWRFA").toCharArray()
	public SGWindow(String filename) throws Exception {
		JFrame win = new JFrame("Schedule Generator");
		win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		win.setResizable( false );
		
		// Read File Lines
		ArrayList<String> lines;
		try {
			lines = readFileLines(filename);
		} catch (Exception e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		}
		
		String days = "MTWRF";
		ArrayList<ArrayList<String>> Times = SGWindow.getDayTimes(lines, days);
		ArrayList<ArrayList<ScheduleTimeRange>> timeRanges = new ArrayList<ArrayList<ScheduleTimeRange>>();
		
		for(int i = 0; i < Times.size(); ++i) {
			ArrayList<ScheduleTimeRange> dayRanges = new ArrayList<ScheduleTimeRange>();
			for(int j = 0; j < Times.get(i).size(); ++j) {
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
			for(int j = 0; j < Times.get(i).size(); ++j) {
				JCheckBox hourBox = new JCheckBox(timeRanges.get(i).get(j).rangeString());
				dayBoxes.add(hourBox);
			}
			checkBoxes.add(dayBoxes);
		}
		
		win.setSize( days.length() * 150, dayLength * 75 );
		win.setLayout( new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();
		c.weightx    = 0;
		c.weighty    = 0;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.ipadx      = (int)win.getSize().getWidth() / (2 * days.length());
		c.ipady      = (int)win.getSize().getHeight() / (2 * dayLength);
		c.fill       = GridBagConstraints.BOTH;
		
		// Weird format; possibly redo?
		for(int i = 0; i < checkBoxes.size(); ++i) {
			for(int j = 0; j < checkBoxes.get(i).size(); ++j) {
				c.gridx = c.gridwidth * i;
				c.gridy = c.gridheight * j;
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
		ArrayList<ArrayList<String>> Times = new ArrayList<ArrayList<String>>();
		for(int i = 0; i < days.length(); ++i) {
			ArrayList<String> dayTimes = new ArrayList<String>();
			for(int j = 0; j < lines.size(); ++j) {
				if(lines.get(j).indexOf('\t') != -1) {
					String lineDays = lines.get(j).substring(0, lines.get(j).indexOf('\t'));
					if(lineDays.indexOf(days.charAt(i)) != -1) {
						String time = lines.get(j).substring(lines.get(j).indexOf('\t') + 1);
						if(!dayTimes.contains(time)) {
							dayTimes.add(time);
						}
					}
				}
			}
			Collections.sort(dayTimes);
			Times.add(dayTimes);
		}
		return Times;
	}
	
	static public ArrayList<String> compileTimes(ArrayList<ArrayList<String>> times) {
		ArrayList<String> compiledTimes = new ArrayList<String>();
		for(int i = 0; i < times.size(); ++i) {
			for(int j = 0; j < times.get(i).size(); ++j) {
				if(!compiledTimes.contains(times.get(i).get(j))) {
					compiledTimes.add(times.get(i).get(j));
				}
			}
		}
		return compiledTimes;
	}
}