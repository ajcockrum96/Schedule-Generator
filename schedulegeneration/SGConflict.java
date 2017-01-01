package schedulegeneration;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.SecurityException;
import java.util.ArrayList;

public class SGConflict implements ActionListener {
	public static final int DEFAULT_X = 350;
	public static final int DEFAULT_Y = 250;
	private JFrame window;
	private ArrayList<String> conflictCourses;
	private String filename;
	private ArrayList<JCheckBox> optionBoxes;

	public SGConflict(ArrayList<String> conflictCourses, String filename) throws Exception {
		this.conflictCourses = conflictCourses;
		this.filename = filename;

		// Set Constraints
		GridBagConstraints c = new GridBagConstraints();
		c.weightx    = 1;
		c.weighty    = 1;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.ipadx      = DEFAULT_X / 15;
		c.ipady      = DEFAULT_Y / 20;
		c.fill       = GridBagConstraints.BOTH;
		int winWidth = 3;

		// Launch Window
		try {
			this.window = new JFrame("Conflict");
			this.window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		} catch(HeadlessException e) {
			System.err.format("%s%n", e);
			throw new Exception("SGConflict constructor failed", e);
		} catch (SecurityException e) {
			System.err.format("%s%n", e);
			throw new Exception("SGWindow constructor failed", e);
		}
		this.window.setResizable( false );
		this.window.setSize( DEFAULT_X, DEFAULT_Y );
		this.window.setLayout( new GridBagLayout() );

		// Add information
		this.optionBoxes = new ArrayList<JCheckBox>();
		int i = 0;
		for(i = 0; i < conflictCourses.size(); ++i) {
			this.optionBoxes.add(new JCheckBox(conflictCourses.get(i), true));
			c.gridy = i / winWidth + 1;
			c.gridx = i % winWidth;
			this.window.add(this.optionBoxes.get(i), c);
		}

		String qString = "The following courses have <i>no</i> time options " +
		                 "that fit within your input preferences.  " +
						 "Please select which courses you would like to include<br>" +
						 "(<b>CAUTION</b>: This removes the preferences for the " +
						 "selected classes.)";
		JLabel question = new JLabel("<html><div style='text-align: center;'>" + qString + "</div></html>");
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = winWidth;
		this.window.add(question, c);

		JButton proceed = new JButton("Proceed");
		proceed.addActionListener( this );
		c.gridx = 0;
		c.gridy = i / winWidth + 1;
		c.fill = GridBagConstraints.NONE;
		this.window.add(proceed, c);
		this.window.getRootPane().setDefaultButton(proceed);
		centerWindow();

		this.window.setVisible( true );
	}

	public void centerWindow() throws Exception {
		try {
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			if(this.window != null) {
				int centerX = (int)(screenSize.getWidth() - this.window.getWidth()) / 2;
				int centerY = (int)(screenSize.getHeight() - this.window.getHeight()) / 2;
				this.window.setLocation(centerX, centerY);
			}
		} catch(AWTError e) {
			System.err.format("%s%n", e);
			throw new Exception("centerWindow failed", e);
		}
	}

	public void actionPerformed( ActionEvent e ) {
		// Hide window
		this.window.setVisible( false );
		// Get Course Names to Exclude
		ArrayList<String> excludeCourses = new ArrayList<String>();
		for(int i = 0; i < optionBoxes.size(); ++i) {
			if(!optionBoxes.get(i).isSelected()) {
				excludeCourses.add(conflictCourses.get(i));
			}
		}
		// Retreive Lines from Preference File
		ArrayList<String> lines;
		try {
			lines = ScheduleGenerator.readInputFile(this.filename);
		} catch(Exception ex) {
			System.out.println("Error, schedules unable to be generated!");
			// CLOSE PROGRAM
			return;
		}
		// Rewrite Lines removing Excluded courses
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.filename));
			for(int i = 0; i < lines.size(); ++i) {
				if(lines.get(i).length() > 0 && Character.isDigit(lines.get(i).charAt(0))) {
					String currCourse = lines.get(i).substring(lines.get(i).indexOf('\t') + 1);
					if(excludeCourses.contains(currCourse)) {
						while(++i < lines.size()) {
							if(lines.get(i).length() > 0 && Character.isDigit(lines.get(i).charAt(0))) {
								--i;
								break;
							}
						}
					}
					else {
						writer.write(lines.get(i));
						writer.newLine();
						writer.flush();
					}
				}
				else {
					writer.write(lines.get(i));
					writer.newLine();
					writer.flush();
				}
			}
		} catch(IOException ex) {
			System.out.println("Error, schedules unable to be generated!");
			this.window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			this.window.dispatchEvent(new WindowEvent(this.window, WindowEvent.WINDOW_CLOSING));
		}
		try {
			ScheduleGenerator.generateSchedule(this.filename);
		} catch(Exception ex) {
			System.out.println("Error, schedules unable to be generated!");
			this.window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
			this.window.dispatchEvent(new WindowEvent(this.window, WindowEvent.WINDOW_CLOSING));
		}
	}
}