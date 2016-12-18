import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SGHelper implements ActionListener {
	int DEFAULT_X = 350;
	int DEFAULT_Y = 250;
	ArrayList<ArrayList<JCheckBox>> boxGrid;
	JFrame      window;
	String      range;
	boolean     []options;
	int         currType = -1;
	String      eventDay = "";
	ActionEvent prevEvent;
	public SGHelper(String range, boolean []options, ArrayList<ArrayList<JCheckBox>> boxGrid, String eventDay) {
		int numTypes  = 1;
		this.range    = range;
		this.options  = options;
		this.boxGrid  = boxGrid;
		this.eventDay = eventDay;

		for(int i = 0; i < numTypes && currType == -1; ++i) {
			if(options[i]) {
				this.buildHelperWindow(i);
			}
		}
	}

	public void buildHelperWindow(int option) {
		// Set Constraints
		GridBagConstraints c = new GridBagConstraints();
		c.weightx    = 1;
		c.weighty    = 1;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.ipadx      = DEFAULT_X / 15;
		c.ipady      = DEFAULT_Y / 20;
		c.fill       = GridBagConstraints.BOTH;

		// Launch Window
		window = new JFrame("Helper");
		window.setResizable( false );
		window.setSize( DEFAULT_X, DEFAULT_Y );
		window.setLayout( new GridBagLayout() );
		
		switch(option) {
			case(0):
				{
				String qString = "Do you wish to uncheck ALL times before or after " + range.substring(0, range.indexOf('-')).trim() + "?";
				JLabel question = new JLabel("<html><div style='text-align: center;'>" + qString + "</div></html>");
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = 3;
				window.add(question, c);

				c.fill = GridBagConstraints.NONE;
				JButton beforeButton = new JButton("Before");
				beforeButton.addActionListener( this );
				c.gridx = 0;
				c.gridy = 1;
				c.gridwidth = 1;
				window.add(beforeButton, c);

				JButton afterButton = new JButton("After");
				afterButton.addActionListener( this );
				c.gridx = 1;
				c.gridy = 1;
				c.gridwidth = 1;
				window.add(afterButton, c);

				JButton noButton = new JButton("Neither");
				noButton.addActionListener( this );
				c.gridx = 2;
				c.gridy = 1;
				c.gridwidth = 1;
				window.add(noButton, c);
				break;
				}
			case(1):
				{
				String qString = "Do you wish to repeate this action for the following days?";
				JLabel question = new JLabel("<html><div style='text-align: center;'>" + qString + "</div></html>");
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = boxGrid.size();
				window.add(question, c);

				c.fill = GridBagConstraints.NONE;
				for(int i = 0; i < boxGrid.size(); ++i) {
					JCheckBox box = new JCheckBox(((JLabel)(boxGrid.get(i).get(0).getParent().getComponent(0))).getText());
					c.gridx = i;
					c.gridy = 1;
					c.gridwidth = 1;
					window.add(box, c);
				}

				JButton yesButton = new JButton("Yes");
				yesButton.addActionListener( this );
				c.gridx = 0;
				c.gridy = 2;
				c.gridwidth = 1;
				window.add(yesButton, c);

				JButton noButton = new JButton("No");
				noButton.addActionListener( this );
				c.gridx = 1;
				c.gridy = 2;
				c.gridwidth = 1;
				window.add(noButton, c);
				break;
				}
		}
		this.centerWindow();
		window.setVisible( true );
		currType = option;
	}

	public void centerWindow() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if(window != null) {
			int centerX = (int)(screenSize.getWidth() - window.getWidth()) / 2;
			int centerY = (int)(screenSize.getHeight() - window.getHeight()) / 2;
			window.setLocation(centerX, centerY);
		}
	}

	public void actionPerformed(ActionEvent e) {
		JButton eventButton = (JButton)(e.getSource());
		String  eventText   = eventButton.getText();
		switch(currType) {
			case(0):
				if(eventText.equals("Before")) {
					String timeString = range.substring(range.indexOf('-') + 1).trim();
					this.uncheckBeforeTime(new ScheduleTime(timeString), eventDay);
				}
				else if(eventText.equals("After")) {
					String timeString = range.substring(0, range.indexOf('-')).trim();
					this.uncheckAfterTime(new ScheduleTime(timeString), eventDay);
				}
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				this.buildHelperWindow(1);
				break;
			case(1):
				if(eventText.equals("Yes")) {
					System.out.println("YAY");
				}
				window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				break;
		}
		prevEvent = e;
	}

	public void uncheckBeforeTime(ScheduleTime time, String days) {
		for(int i = 0; i < boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = 0; j < boxGrid.get(i).size(); ++j) {
					JCheckBox    currBox  = boxGrid.get(i).get(j);
					ScheduleTime currTime = new ScheduleTime(currBox.getText().substring(0, currBox.getText().indexOf('-')).trim());
					if(ScheduleTime.compareTimes(currTime, time) <= 0) {
						currBox.setSelected(false);
					}
					else {
						break;
					}
				}
			}
		}
	}

	public void uncheckAfterTime(ScheduleTime time, String days) {
		for(int i = 0; i < boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = boxGrid.get(i).size() - 1; j >= 0; --j) {
					JCheckBox    currBox  = boxGrid.get(i).get(j);
					ScheduleTime currTime = new ScheduleTime(currBox.getText().substring(0, currBox.getText().indexOf('-')).trim());
					if(ScheduleTime.compareTimes(currTime, time) >= 0) {
						currBox.setSelected(false);
					}
					else {
						break;
					}
				}
			}
		}
	}
}