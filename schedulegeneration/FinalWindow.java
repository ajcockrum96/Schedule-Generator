package schedulegeneration;

// import javax.swing.*;
import javax.swing.JLabel;
import javax.swing.JFrame;

// import java.awt.*;
import java.awt.AWTError;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Toolkit;

// import java.lang.*;
import java.lang.SecurityException;

public class FinalWindow {
	JFrame win;
	public FinalWindow(int scheduleCount) throws Exception {
		try {
			this.win = new JFrame("Schedule Results");
			this.win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		} catch (HeadlessException e) {
			System.err.format("%s%n", e);
			throw new Exception("FinalWindow constructor failed", e);
		} catch (SecurityException e) {
			System.err.format("%s%n", e);
			throw new Exception("FinalWindow constructor failed", e);
		}

		this.win.setSize( 300, 200 );
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
					" <br> which matches the colors with class names.";
		this.win.add(new JLabel("<html><div style='text-align: center;'>" + info + "</div></html>"), c);
		this.centerWindow();
		this.win.setVisible( true );
	}

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