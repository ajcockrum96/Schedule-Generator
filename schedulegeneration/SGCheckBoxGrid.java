package schedulegeneration;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * <p>
 * An object containing a 2D ArrayList of check boxes that represent time
 * options for a schedule.
 * </p>
 * <p>
 * The check boxes are organized dynamically based on the days used and the
 * number of valid time ranges for each day.  Additionally, the check boxes
 * are preset onto JPanels for each day, and one overall JPanel so that the
 * grid can be easily added to a Container.
 * </p>
 * @see SGWindow
 */
public class SGCheckBoxGrid implements ActionListener {
	/**
	 * 2D Representation of current check box grid
	 */
	public ArrayList<ArrayList<JCheckBox>> boxGrid;

	/**
	 * JPanel containing the JCheckBox objects in boxGrid
	 */
	public JPanel fullGrid;

	/**
	 * Length (in check boxes) of the longest day
	 */
	protected int dayRange;

	/**
	 * SGHelper instance created for every unchecking action
	 */
	private SGHelper helper;

	/**
	 * SGHelper constructor information regarding user preferences
	 */
	protected boolean options[] = {true, true};

	/**
	 * <p>
	 * Constructs a new SGCheckBoxGrid with the given time ranges in columns
	 * given by daysUsed.
	 *
	 * The timeRanges given are made into JCheckBox objects and put into
	 * ArrayLists for each of the days they apply that are in daysUsed. These
	 * ArrayLists make up the 2D boxGrid.
	 * </p>
	 * <p>
	 * After these lists are created, the check boxes are then added to JPanel
	 * instances for each day. These JPanels each contain a JLabel as their first
	 * element to identify them with their corresponding day. One parent
	 * JPanel contains all of these containers.
	 * </p>
	 *
	 * @param timeRanges	the ArrayList of time ranges to assign to check boxes
	 * @param daysUsed		the String of day columns to sort the check boxes into
	 */
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

	/**
	 * Launches SGHelper instance if a JCheckBox is unselected; other actions
	 * are ignored.
	 *
	 * @param ae	the ActionEvent that invoked the method
	 */
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