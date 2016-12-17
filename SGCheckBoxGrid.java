import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SGCheckBoxGrid implements ActionListener {
	ArrayList<ArrayList<JCheckBox>> boxGrid;

	public SGCheckBoxGrid(ArrayList<ScheduleTimeRange> timeRanges, String daysUsed) {
		boxGrid = new ArrayList<ArrayList<JCheckBox>>();
		for(int i = 0; i < daysUsed.length(); ++i) {
			ArrayList<JCheckBox> dayBoxes = new ArrayList<JCheckBox>();
			for(int j = 0; j < timeRanges.size(); ++j) {
				ScheduleTimeRange currRange = timeRanges.get(j);
				if(currRange.getDays().indexOf(daysUsed.charAt(i)) != -1) {
					JCheckBox currBox = new JCheckBox(ScheduleTimeRange.convert24To12HourRange(currRange.rangeString()), true);
					currBox.addActionListener(this);
					dayBoxes.add(currBox);
				}
			}
			boxGrid.add(dayBoxes);
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