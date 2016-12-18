import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SGCheckBoxGrid implements ActionListener {
	JPanel fullGrid;
	int    dayRange;

	public SGCheckBoxGrid(ArrayList<ScheduleTimeRange> timeRanges, String daysUsed) {
		ArrayList<ArrayList<ScheduleTimeRange>> dayRanges = new ArrayList<ArrayList<ScheduleTimeRange>>();
		for(int i = 0; i < daysUsed.length(); ++i) {
			ArrayList<ScheduleTimeRange> currRanges = new ArrayList<ScheduleTimeRange>();
			for(int j = 0; j < timeRanges.size(); ++j) {
				if(timeRanges.get(j).getDays().indexOf(daysUsed.charAt(i)) != -1) {
					currRanges.add(new ScheduleTimeRange(timeRanges.get(j).rangeString(), daysUsed.substring(i, i + 1)));
				}
			}
			dayRanges.add(currRanges);
		}

		fullGrid = new JPanel();
		fullGrid.setLayout( new GridLayout(1, dayRanges.size()) );
		dayRange = 0;
		for(int i = 0; i < dayRanges.size(); ++i) {
			int j = 0;
			JPanel dayGrid = new JPanel();
			dayGrid.setLayout( new GridLayout(dayRanges.get(i).size() + 1, 1) );
			dayGrid.add( new JLabel(daysUsed.substring(i, i + 1)) );
			for(j = 0; j < dayRanges.get(i).size(); ++j) {
				dayGrid.add( new JCheckBox(dayRanges.get(i).get(j).rangeString(), true) );
			}
			if(j > dayRange) {
				dayRange = j;
			}
			fullGrid.add(dayGrid);
		}
	}

	public void actionPerformed( ActionEvent e ) {
		JCheckBox eventBox  = (JCheckBox)e.getSource();
		String    eventText = eventBox.getText();
		System.out.println(eventText);
		if(!eventBox.isSelected()) {
			JFrame reqWin = new JFrame("Helper");
			reqWin.setResizable( false );
		 
			reqWin.setSize( 200, 150 );
			reqWin.setLayout( new GridLayout(2, 1) );
			JLabel question = new JLabel(String.format("Do you wish to uncheck ALL times before %s on the following days of the week?", eventText.substring(0, eventText.indexOf('-'))));
			reqWin.add(question);
			JButton yesButton = new JButton("Yes");
			reqWin.add(yesButton);

			reqWin.setVisible( true );
		}
	}
}