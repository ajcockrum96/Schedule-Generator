package purduecrsinfo;
import schedulegeneration.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.lang.SecurityException;
import java.util.Calendar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

public class PurdueCoursePrompt extends JFrame implements ActionListener {
	/**
	 * Prompt window
	 */
	private JFrame win;

	/**
	 * Term options available
	 */
	private static String []termOptions = {"Fall", "Spring", "Summer"};

	/**
	 * Term selection
	 */
	private JComboBox terms;

	/**
	 * Current year
	 */
	private Integer currYear;

	/**
	 * Number of year options available
	 */
	private static Integer yearCount = 5;

	/**
	 * Year options available
	 */
	private Integer []yearOptions = {0, 0, 0, 0, 0};

	/**
	 * Year selection
	 */
	private JComboBox years;

	/**
	 * ArrayList of Course Subject text fields
	 */
	private ArrayList<JTextField> courseSubjects;

	/**
	 * ArrayList of Course Number text fields
	 */
	private ArrayList<JTextField> courseNumbers;

	/**
	 * Submit Button
	 */
	private JButton submit;

	/**
	 * Course Parsers
	 */
	private ArrayList<PurdueCourseParser> parsedInfo;

	/**
	 * Output filename
	 */
	private String outputFilename;

	/**
	 * Course output count
	 */
	public Integer outputCourseCount;

	public PurdueCoursePrompt() throws Exception {
		this(7, "rawInput.txt");
	}

	public PurdueCoursePrompt(Integer numCourses, String outputFilename) throws Exception {
		try {
			this.win = new JFrame("Purdue Course Prompt");
			this.win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		} catch (HeadlessException e) {
			System.err.format("%s%n", e);
			throw new Exception("PurdueCoursePrompt constructor failed", e);
		} catch (SecurityException e) {
			System.err.format("%s%n", e);
			throw new Exception("PurdueCoursePrompt constructor failed", e);
		}

		this.outputFilename = outputFilename;
		this.outputCourseCount = 0;

		// Set year options
		Calendar now = Calendar.getInstance();
		this.currYear = now.get(Calendar.YEAR);
		for(int i = 0; i < yearCount; ++i) {
			this.yearOptions[i] = currYear - yearCount + i + 2;
		}

		// Configure window setup
		this.win.setSize( 700, 300 );
		this.centerWindow();
		this.win.setLayout( new GridBagLayout() );

		// Initialize Object constraints
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.ipadx   = 100; 
		c.ipady   = 5; 
		c.fill    = GridBagConstraints.HORIZONTAL;

		// Place overall Settings on Top Panel
		JPanel topPanel = new JPanel();
		topPanel.setLayout( new GridBagLayout() );

		this.terms = new JComboBox(termOptions);
		this.terms.setFocusable(false);
		c.gridx      = 0;
		c.gridy      = 0;
		c.gridwidth  = 5;
		c.gridheight = 1;
		topPanel.add(this.terms, c);

		this.years = new JComboBox(yearOptions);
		this.years.setSelectedIndex(yearCount - 2);
		this.years.setFocusable(false);
		c.gridx     += c.gridwidth;
		c.gridy      = 0;
		c.gridwidth  = 5;
		c.gridheight = 1;
		topPanel.add(this.years, c);

		// Add Top Panel
		c.gridx      = 0;
		c.gridy      = 0;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.weightx    = 1;
		c.weighty    = 0;
		c.fill       = GridBagConstraints.BOTH;
		this.win.add(topPanel, c);

		// Place Course Settings on Middle Panel
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout( new GridBagLayout() );

		c.weightx = 1;
		c.weighty = 1;
		c.fill    = GridBagConstraints.HORIZONTAL;
		this.courseSubjects = new ArrayList<JTextField>();
		this.courseNumbers  = new ArrayList<JTextField>();
		for(int i = 0; i < numCourses; ++i) {
			c.gridx      = 1;
			c.gridy      = i;
			c.gridwidth  = 2;
			c.gridheight = 1;
			c.weightx    = c.gridwidth / 5.0;
			JTextField subject = new JTextField();
			middlePanel.add(subject, c);
			this.courseSubjects.add(subject);
			c.gridx     += c.gridwidth;
			c.gridy      = i;
			c.gridwidth  = 3;
			c.gridheight = 1;
			c.weightx    = c.gridwidth / 5.0;
			JTextField number = new JTextField();
			middlePanel.add(number, c);
			this.courseNumbers.add(number);
		}

		// Add Middle Panel
		c.gridx      = 0;
		c.gridy      = 1;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.fill       = GridBagConstraints.BOTH;
		this.win.add(middlePanel, c);

		// Place Submit Button on Bottom Panel
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout( new GridBagLayout() );

		this.submit = new JButton("Submit");
		c.gridx      = 0;
		c.gridy      = 0;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.fill       = GridBagConstraints.NONE;
		bottomPanel.add(this.submit, c);
		this.submit.addActionListener(this);
		this.win.getRootPane().setDefaultButton(this.submit);

		// Add Middle Panel
		c.gridx      = 0;
		c.gridy      = 2;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.weightx    = 0;
		c.weighty    = 0;
		this.win.add(bottomPanel, c);

		this.win.setResizable( false );
		this.win.setVisible( true );
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
			if(this.win != null) {
				int centerX = (int)(screenSize.getWidth() - this.win.getWidth()) / 2;
				int centerY = (int)(screenSize.getHeight() - this.win.getHeight()) / 2;
				this.win.setLocation(centerX, centerY);
			}
		} catch(AWTError e) {
			System.err.format("%s%n", e);
			throw new Exception("centerWindow failed", e);
		}
	}

	public void actionPerformed(ActionEvent ae) {
		this.win.setVisible( false );
		String term = (String)(this.terms.getSelectedItem());
		String termSelected = "";
		if(term.equals(termOptions[0])) {
			termSelected = PurdueCourseInfo.FALL;
		}
		else if(term.equals(termOptions[1])) {
			termSelected = PurdueCourseInfo.SPRING;
		}
		else if(term.equals(termOptions[2])) {
			termSelected = PurdueCourseInfo.SUMMER;
		}
		else {
			termSelected = PurdueCourseInfo.FALL;
		}

		String directoryName = "HTMLFiles";
		File directory = new File(directoryName);
		this.parsedInfo = new ArrayList<PurdueCourseParser>();
		if(!directory.isDirectory()) {
			directory.mkdir();
		}
		for(int i = 0; i < courseNumbers.size(); ++i) {
			String currSubject = courseSubjects.get(i).getText();
			String currNumber  = courseNumbers.get(i).getText();
			if(!currSubject.equals("") && !currNumber.equals("")) {
				PurdueCourseInfo curr = new PurdueCourseInfo((Integer)(this.years.getSelectedItem()), termSelected, currSubject, currNumber);
				try {
					System.out.println("HTTP Response Code: " + curr.acquireHtmlFromInternet());
					String htmlfilepath = "." + File.separatorChar + directoryName + File.separatorChar + currSubject + currNumber + ".html";
					FileWriter fw = new FileWriter(htmlfilepath);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write(curr.getRawHtml());
					bw.close();
					PurdueCourseParser currParse = new PurdueCourseParser(htmlfilepath);
					this.parsedInfo.add(currParse);
				} catch(Exception e) {
					System.out.format("Ouch\n");
				}
			}
		}
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(this.outputFilename));
			for(int i = 0; i < parsedInfo.size(); ++i) {
				// Generate "input file"
				ArrayList<String> currStrings = parsedInfo.get(i).generateOutputStrings();
				for(int j = 0; j < currStrings.size(); ++j) {
					writer.write(currStrings.get(j));
					writer.newLine();
					writer.flush();
				}
				writer.newLine();
				writer.flush();
			}
			writer.close();
		} catch(IOException ioe) {
			System.err.format("%s\n", ioe);
		}
		// Add this line for when the program calls the schedule generator function
		try {
			SGWindow window = new SGWindow(this.outputFilename, "preferredInput.txt");
			this.win.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );
			this.win.dispatchEvent(new WindowEvent(this.win, WindowEvent.WINDOW_CLOSING));
		} catch(Exception e) {
			System.err.format("%d\n", e);
			this.win.dispatchEvent(new WindowEvent(this.win, WindowEvent.WINDOW_CLOSING));
		}
	}
}