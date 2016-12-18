import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SGCheckBoxGrid implements ActionListener {
	ArrayList<ArrayList<JCheckBox>> boxGrid;
	JPanel fullGrid;
	int    dayRange;

	public SGCheckBoxGrid(ArrayList<ScheduleTimeRange> timeRanges, String daysUsed) {
		boxGrid = new ArrayList<ArrayList<JCheckBox>>();
		for(int i = 0; i < daysUsed.length(); ++i) {
			ArrayList<JCheckBox> currBoxes = new ArrayList<JCheckBox>();
			for(int j = 0; j < timeRanges.size(); ++j) {
				if(timeRanges.get(j).getDays().indexOf(daysUsed.charAt(i)) != -1) {
					JCheckBox currBox = new JCheckBox(ScheduleTimeRange.convert24To12HourRange(timeRanges.get(j).rangeString()), true);
					currBox.addActionListener( this );
					currBoxes.add( currBox );
				}
			}
			boxGrid.add(currBoxes);
		}

		fullGrid = new JPanel();
		fullGrid.setLayout( new GridLayout(1, boxGrid.size()) );
		dayRange = 0;
		for(int i = 0; i < boxGrid.size(); ++i) {
			int j = 0;
			JPanel dayGrid = new JPanel();
			dayGrid.setLayout( new GridLayout(boxGrid.get(i).size() + 1, 1) );
			dayGrid.add( new JLabel(daysUsed.substring(i, i + 1)) );
			for(j = 0; j < boxGrid.get(i).size(); ++j) {
				dayGrid.add( boxGrid.get(i).get(j) );
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