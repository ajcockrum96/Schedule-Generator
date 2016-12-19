// import javax.swing.*;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

// import java.awt.*;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;

// import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

// import java.io.*;

// import java.util.*;
import java.util.ArrayList;

public class SGHelper implements ActionListener {
	static int NUM_TYPES = 2;
	int        DEFAULT_X = 350;
	int        DEFAULT_Y = 250;
	SGCheckBoxGrid       checkBoxes;
	JFrame               window;
	String               range;
	String               eventDay;
	int                  currType;
	ActionEvent          prevEvent;
	ArrayList<JCheckBox> dayBoxes;
	ArrayList<JCheckBox> optionBoxes;

	public SGHelper(String range, String eventDay, SGCheckBoxGrid checkBoxes) throws Exception {
		this.range      = range;
		this.eventDay   = eventDay;
		this.checkBoxes = checkBoxes;
		optionBoxes     = new ArrayList<JCheckBox>();

		for(int i = 0; i < NUM_TYPES; ++i) {
			optionBoxes.add(new JCheckBox("Don't ask me this again"));
		}

		for(int i = 0; i < NUM_TYPES; ++i) {
			if(checkBoxes.options[i]) {
				try {
				this.buildHelperWindow(i);
				} catch(Exception e) {
					System.err.format("%s%n", e);
					throw new Exception("SGHelper constructor failed", e);
				}
				break;
			}
		}
	}

	public void buildHelperWindow(int option) throws Exception {
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
		try {
			window = new JFrame("Helper");
		} catch(HeadlessException e) {
			System.err.format("%s%n", e);
			throw new Exception("SGHelper buildHelperWindow failed", e);
		}
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

				c.fill = GridBagConstraints.BOTH;
				c.gridx = 0;
				c.gridy = 2;
				c.gridwidth = 3;
				window.add(optionBoxes.get(option), c);

				window.getRootPane().setDefaultButton(beforeButton);
				break;
				}
			case(1):
				{
				String qString = "Do you wish to repeate this action for the following days?";
				JLabel question = new JLabel("<html><div style='text-align: center;'>" + qString + "</div></html>");
				c.gridx = 0;
				c.gridy = 0;
				c.gridwidth = checkBoxes.boxGrid.size();
				window.add(question, c);

				c.fill = GridBagConstraints.NONE;
				dayBoxes = new ArrayList<JCheckBox>();
				for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
					JCheckBox box = new JCheckBox(((JLabel)(checkBoxes.boxGrid.get(i).get(0).getParent().getComponent(0))).getText(), true);
					c.gridx = i;
					c.gridy = 1;
					c.gridwidth = 1;
					dayBoxes.add(box);
					window.add(box, c);
				}

				JButton yesButton = new JButton("Yes");
				yesButton.addActionListener( this );
				c.gridx = 0;
				c.gridy = 2;
				c.gridwidth = checkBoxes.boxGrid.size() / 2;
				window.add(yesButton, c);

				JButton noButton = new JButton("No");
				noButton.addActionListener( this );
				c.gridx = c.gridwidth;
				if(checkBoxes.boxGrid.size() % 2 != 0) {
					++(c.gridx);
				}
				c.gridy = 2;
				c.gridwidth = checkBoxes.boxGrid.size() / 2;
				window.add(noButton, c);

				c.fill = GridBagConstraints.BOTH;
				c.gridx = 0;
				c.gridy = 3;
				c.gridwidth = checkBoxes.boxGrid.size();
				window.add(optionBoxes.get(option), c);

				window.getRootPane().setDefaultButton(yesButton);
				break;
				}
		}
		try {
			this.centerWindow();
		} catch(Exception e) {
			System.out.println("Error, window could not be centered!");
		}
		window.setVisible( true );
		currType = option;
	}

	public void centerWindow() throws Exception {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if(window != null) {
				int centerX = (int)(screenSize.getWidth() - window.getWidth()) / 2;
				int centerY = (int)(screenSize.getHeight() - window.getHeight()) / 2;
				window.setLocation(centerX, centerY);
			}
		} catch(AWTError e) {
			System.err.format("%s%n", e);
			throw new Exception("centerWindow failed", e);
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
				checkBoxes.options[currType] = !optionBoxes.get(currType).isSelected();
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				break;
			case(1):
				if(eventText.equals("Yes")) {
					String eventDays = "";
					for(int i = 0; i < dayBoxes.size(); ++i) {
						if(dayBoxes.get(i).isSelected()) {
							eventDays = eventDays + dayBoxes.get(i).getText();
						}
					}
					JButton prevButton = (JButton)prevEvent.getSource();
					if(prevButton.getText().equals("Before")) {
						String timeString = range.substring(range.indexOf('-') + 1).trim();
						this.uncheckBeforeTime(new ScheduleTime(timeString), eventDays);
					}
					if(prevButton.getText().equals("After")) {
						String timeString = range.substring(0, range.indexOf('-')).trim();
						this.uncheckAfterTime(new ScheduleTime(timeString), eventDays);
					}
					if(prevButton.getText().equals("Neither")) {
						String timeString = range.substring(0, range.indexOf('-')).trim();
						this.uncheckDuringTime(new ScheduleTime(timeString), eventDays);
					}
				}
				checkBoxes.options[currType] = !optionBoxes.get(currType).isSelected();
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				break;
		}
		prevEvent = e;
		for(int i = currType + 1; i < NUM_TYPES; ++i) {
			if(checkBoxes.options[i]) {
				try {
					this.buildHelperWindow(i);
				} catch(Exception ex) {
					System.out.println("Error, helper window could not be launched!");
				}
				break;
			}
		}
	}

	public void uncheckBeforeTime(ScheduleTime time, String days) {
		for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(checkBoxes.boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = 0; j < checkBoxes.boxGrid.get(i).size(); ++j) {
					JCheckBox    currBox  = checkBoxes.boxGrid.get(i).get(j);
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
		for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(checkBoxes.boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = checkBoxes.boxGrid.get(i).size() - 1; j >= 0; --j) {
					JCheckBox    currBox  = checkBoxes.boxGrid.get(i).get(j);
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

	public void uncheckDuringTime(ScheduleTime time, String days) {
		for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(checkBoxes.boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = 0; j < checkBoxes.boxGrid.get(i).size(); ++j) {
					JCheckBox    currBox  = checkBoxes.boxGrid.get(i).get(j);
					ScheduleTime currTime = new ScheduleTime(currBox.getText().substring(0, currBox.getText().indexOf('-')).trim());
					if(ScheduleTime.compareTimes(currTime, time) == 0) {
						currBox.setSelected(false);
						break;
					}
				}
			}
		}
	}
}