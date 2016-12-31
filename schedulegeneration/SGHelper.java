package schedulegeneration;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.JLabel;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * <p>
 * An object that launches helper windows that aid in time preference selection
 * on an {@link SGCheckBoxGrid} object.
 *
 * These instances are assumed to be built off of a SGCheckBoxGrid instance and
 * will not behave properly otherwise.
 * </p>
 * <p>
 * The following options are implemented and delegate their work to private
 * functions:
 * </p>
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;0) Offers to uncheck boxes before or after the current time box that
 * was unselected.  It also gives a "neither" option if you only wish to operate
 * with time ranges at the current time.
 * </p>
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Before option uses end time of current time range.
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;After option uses end time of current time range.
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Neither option uses start time of current time range.
 * </p>
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;1) Offers to repeat previous action on certain days. A check box
 * is generated for each day that is found in the SGCheckBoxGrid. If the user
 * selects no, then no extra actions are taken. If the user selects yes:
 * </p>
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If option 0 was launched prior to this, then the previous action,
 * which is saved in the prevEvent static field, will be repeated for the
 * selected days, which are stored in the optionBoxes static field.
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If option 0 was <b>not</b> launched prior to this, then this
 * prompt will act as if "neither" was selected in the option 0 prompt. In other
 * words, only the ranges that start at the same time will be effected.
 * </p>
 */
public class SGHelper implements ActionListener {
	/**
	 * Number of prompt types able to be generated
	 */
	private static final int NUM_TYPES = 2;

	/**
	 * Default Width of the window
	 */
	public static final int DEFAULT_X = 350;

	/**
	 * Default Height of the window
	 */
	public static final int DEFAULT_Y = 250;

	/**
	 * SGCheckBoxGrid Object that generated this SGHelper
	 */
	private SGCheckBoxGrid checkBoxes;

	/**
	 * Prompt window
	 */
	private JFrame window;

	/**
	 * Time range of check box that was unselected
	 */
	protected String rangeString;

	/**
	 * Day of check box that was unselected
	 */
	protected String eventDay;

	/**
	 * Current prompt type
	 */
	protected int currType;

	/**
	 * Previous action taken by SGHelper on SGCheckBoxGrid
	 */
	private ActionEvent prevEvent;

	/**
	 * Days available on SGCheckBoxGrid
	 */
	private ArrayList<JCheckBox> dayBoxes;

	/**
	 * Check boxes for prompt window
	 */
	private ArrayList<JCheckBox> optionBoxes;

	/**
	 * <p>
	 * Constructs a new SGHelper based around the given time range, day(s), and
	 * {@link SGCheckBoxGrid}.
	 *
	 * When the SGHelper is created, the first "allowed" prompt type is launched.
	 * If no options are "allowed", then the SGHelper launches nothing.
	 * </p>
	 *
	 * @param  rangeString	the String containing the range for this SGHelper
	 * @param  eventDay		the String of day(s) for this SGHelper
	 * @param  checkBoxes	the SGCheckBoxGrid instance that constructed this SGHelper
	 * @throws Exception	If buildHelperWindow fails and throws Exception
	 */
	public SGHelper(String rangeString, String eventDay, SGCheckBoxGrid checkBoxes) throws Exception {
		this.rangeString = rangeString;
		this.eventDay    = eventDay;
		this.checkBoxes  = checkBoxes;
		optionBoxes      = new ArrayList<JCheckBox>();

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

	/**
	 * <p>
	 * Creates and launches a centered prompt window, filling it with elements based on
	 * the prompt option chosen.
	 *
	 * See the class description for prompt types and the options they contain.
	 * </p>
	 *
	 * @param  option		the prompt option type to be built
	 * @throws Exception	If JFrame cannot be constructed due to HeadlessException
	 */
	private void buildHelperWindow(int option) throws Exception {
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
				String qString = "Do you wish to uncheck ALL times before or after " + rangeString.substring(0, rangeString.indexOf('-')).trim() + "?";
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

	/**
	 * Centers JFrame in window, if the Toolkit is "obtainable".
	 *
	 * @throws Exception	If the Toolkit could not be obtained
	 * @see    Toolkit
	 */
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

	/**
	 * Takes action of current prompt window based on user input and launches
	 * next prompt window.
	 *
	 * The next prompt window that is launched follows the order of increasing
	 * numbers. However, a window type will be skipped if the element of the
	 * option array in the {@link SGCheckBoxGrid} is false, which occurs when the
	 * "Don't ask me this again" box is checked on a prompt window.
	 *
	 * @param e		the ActionEvent that invoked the method
	 */
	public void actionPerformed(ActionEvent e) {
		JButton eventButton = (JButton)(e.getSource());
		String  eventText   = eventButton.getText();
		switch(currType) {
			case(0):
				if(eventText.equals("Before")) {
					String timeString = rangeString.substring(rangeString.indexOf('-') + 1).trim();
					this.uncheckBeforeTime(new SGTime(timeString), eventDay);
				}
				else if(eventText.equals("After")) {
					String timeString = rangeString.substring(0, rangeString.indexOf('-')).trim();
					this.uncheckAfterTime(new SGTime(timeString), eventDay);
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
					if(checkBoxes.options[currType - 1]) {
						JButton prevButton = (JButton)prevEvent.getSource();
						if(prevButton.getText().equals("Before")) {
							String timeString = rangeString.substring(rangeString.indexOf('-') + 1).trim();
							this.uncheckBeforeTime(new SGTime(timeString), eventDays);
						}
						if(prevButton.getText().equals("After")) {
							String timeString = rangeString.substring(0, rangeString.indexOf('-')).trim();
							this.uncheckAfterTime(new SGTime(timeString), eventDays);
						}
						if(prevButton.getText().equals("Neither")) {
							String timeString = rangeString.substring(0, rangeString.indexOf('-')).trim();
							this.uncheckDuringTime(new SGTime(timeString), eventDays);
						}
					}
					else {
						String timeString = rangeString.substring(0, rangeString.indexOf('-')).trim();
						this.uncheckDuringTime(new SGTime(timeString), eventDays);
					}
				}
				checkBoxes.options[currType] = !optionBoxes.get(currType).isSelected();
				// CHECK ME AND MAKE SURE I'M OKAY
				window.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				break;
		}
		prevEvent = e;
		// Launch next "allowed" window or exit if out of types
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

	/**
	 * Unchecks all boxes that contain a time range that starts at or before the
	 * time of the given SGTime.
	 *
	 * @param time		the SGTime to compare others against
	 * @param days		the String of days to modify
	 */
	private void uncheckBeforeTime(SGTime time, String days) {
		for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(checkBoxes.boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = 0; j < checkBoxes.boxGrid.get(i).size(); ++j) {
					JCheckBox    currBox  = checkBoxes.boxGrid.get(i).get(j);
					SGTime currTime = new SGTime(currBox.getText().substring(0, currBox.getText().indexOf('-')).trim());
					// Assume the check boxes are in ascending order
					if(SGTime.compareTimes(currTime, time) <= 0) {
						currBox.setSelected(false);
					}
					else {
						break;
					}
				}
			}
		}
	}

	/**
	 * Unchecks all boxes that contain a time range that ends at or after the
	 * time of the given SGTime.
	 *
	 * @param time		the SGTime to compare others against
	 * @param days		the String of days to modify
	 */
	private void uncheckAfterTime(SGTime time, String days) {
		for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(checkBoxes.boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = checkBoxes.boxGrid.get(i).size() - 1; j >= 0; --j) {
					JCheckBox    currBox  = checkBoxes.boxGrid.get(i).get(j);
					SGTime currTime = new SGTime(currBox.getText().substring(0, currBox.getText().indexOf('-')).trim());
					// Assume the check boxes are in ascending order
					if(SGTime.compareTimes(currTime, time) >= 0) {
						currBox.setSelected(false);
					}
					else {
						break;
					}
				}
			}
		}
	}

	/**
	 * Unchecks all boxes that contain a time range that starts at the time
	 * of the given SGTime.
	 *
	 * @param time		the SGTime to compare others against
	 * @param days		the String of days to modify
	 */
	private void uncheckDuringTime(SGTime time, String days) {
		for(int i = 0; i < checkBoxes.boxGrid.size(); ++i) {
			if(days.contains(((JLabel)(checkBoxes.boxGrid.get(i).get(0).getParent().getComponent(0))).getText())) {
				for(int j = 0; j < checkBoxes.boxGrid.get(i).size(); ++j) {
					JCheckBox    currBox  = checkBoxes.boxGrid.get(i).get(j);
					SGTime currTime = new SGTime(currBox.getText().substring(0, currBox.getText().indexOf('-')).trim());
					// Assume the check boxes are in ascending order
					if(SGTime.compareTimes(currTime, time) == 0) {
						currBox.setSelected(false);
						break;
					}
				}
			}
		}
	}
}