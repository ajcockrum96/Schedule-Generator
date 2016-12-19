// import javax.swing.*;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

// import java.awt.*;
import java.awt.GridLayout;

// import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// import java.io.*;

// import java.util.*;
import java.util.ArrayList;

public class SGCheckBoxGrid implements ActionListener {
	ArrayList<ArrayList<JCheckBox>> boxGrid;
	JPanel fullGrid;
	int    dayRange;
	SGHelper helper;
	boolean options[] = {true, true};

	public SGCheckBoxGrid(ArrayList<ScheduleTimeRange> timeRanges, String daysUsed) {
		this.boxGrid = new ArrayList<ArrayList<JCheckBox>>();
		for(int i = 0; i < daysUsed.length(); ++i) {
			ArrayList<JCheckBox> currBoxes = new ArrayList<JCheckBox>();
			for(int j = 0; j < timeRanges.size(); ++j) {
				if(timeRanges.get(j).getDays().indexOf(daysUsed.charAt(i)) != -1) {
					ScheduleTimeRange currRange = timeRanges.get(j);
					JCheckBox currBox = new JCheckBox(ScheduleTimeRange.convert24To12HourRange(timeRanges.get(j).rangeString()), true);
					currBox.addActionListener( this );
					currBoxes.add( currBox );
				}
			}
			this.boxGrid.add(currBoxes);
		}

		this.fullGrid = new JPanel();
		this.fullGrid.setLayout( new GridLayout(1, this.boxGrid.size()) );
		this.dayRange = 0;
		for(int i = 0; i < this.boxGrid.size(); ++i) {
			int j = 0;
			JPanel dayGrid = new JPanel();
			dayGrid.setLayout( new GridLayout(this.boxGrid.get(i).size() + 1, 1) );
			dayGrid.add( new JLabel(daysUsed.substring(i, i + 1)) );
			for(j = 0; j < this.boxGrid.get(i).size(); ++j) {
				dayGrid.add( this.boxGrid.get(i).get(j) );
			}
			if(j > this.dayRange) {
				this.dayRange = j;
			}
			this.fullGrid.add(dayGrid);
		}
	}

	public void actionPerformed( ActionEvent ae ) {
		JCheckBox eventBox  = (JCheckBox)ae.getSource();
		String    eventText = eventBox.getText();
		String    eventDay  = ((JLabel)(eventBox.getParent().getComponent(0))).getText();
		if(!eventBox.isSelected()) {
			try {
				this.helper = new SGHelper(eventText, eventDay, this);
			} catch(Exception e) {
				System.out.println("Error, helper window could not be launched!");
			}
		}
	}
}