package schedulegeneration;

import javax.swing.JLabel;
import javax.swing.JFrame;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.lang.SecurityException;

/**
 * <p>
 * A simple window wrapper that informs the user that the schedule generation
 * operation has completed.
 *
 * The following information is included:
 * </p>
 * <p>
 * &nbsp;&nbsp;&nbsp;&nbsp;-The number of schedules generated
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;-Where to find the schedule image files
 * <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;-Where to find the schedule key image file
 * </p>
 */
public class SGFinal {
	/**
	 * Pop-up window
	 */
	private JFrame win;

	/**
	 * Default Width of the window
	 */
	public static final int DEFAULT_X = 300;

	/**
	 * Default Height of the window
	 */
	public static final int DEFAULT_Y = 200;

	/**
	 * Constructs a new SGFinal that launches an informational window.
	 *
	 * @param  scheduleCount	the number of schedules generated
	 * @throws Exception		If JFrame cannot be constructed or if JFrame.EXIT_ON_CLOSE causes a SecurityException
	 * @see    JFrame
	 */
	public SGFinal(int scheduleCount) throws Exception {
		try {
			this.win = new JFrame("Schedule Results");
			this.win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		} catch (HeadlessException e) {
			System.err.format("%s%n", e);
			throw new Exception("SGFinal constructor failed", e);
		} catch (SecurityException e) {
			System.err.format("%s%n", e);
			throw new Exception("SGFinal constructor failed", e);
		}

		this.win.setSize( this.DEFAULT_X, this.DEFAULT_Y );
		this.win.setResizable( false );
		this.win.setLayout( new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();
		c.gridx      = 0;
		c.gridy      = 0;
		c.gridwidth  = 1;
		c.gridheight = 1;
		c.fill       = GridBagConstraints.BOTH;
		c.weightx    = 1;
		c.weighty    = 1;
		c.ipadx      = 1;
		c.ipady      = 1;

		String info = String.format("%d Schedules Generated.", scheduleCount) +
					" <br><br> The Generated Schedules can be found in the" +
					" <br> \"Images\" Folder along with \"keys.png\"" +
					" <br> which matches the colors with course names.";
		this.win.add(new JLabel("<html><div style='text-align: center;'>" + info + "</div></html>"), c);
		this.centerWindow();
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
			if(win != null) {
				int centerX = (int)(screenSize.getWidth() - win.getWidth()) / 2;
				int centerY = (int)(screenSize.getHeight() - win.getHeight()) / 2;
				win.setLocation(centerX, centerY);
			}
		} catch(AWTError e) {
			System.err.format("%s%n", e);
			throw new Exception("centerWindow failed", e);
		}
	}
}