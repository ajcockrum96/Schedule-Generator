import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class SGHelper implements ActionListener {
	int DEFAULT_X = 350;
	int DEFAULT_Y = 250;
	JFrame  window;
	String  range;
	boolean []options;
	int     currType = -1;
	public SGHelper(String range, boolean []options) {
		int numTypes  = 1;
		this.range = range;
		this.options = options;

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
		System.out.println(currType);
		switch(currType) {
			case(0):
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				this.buildHelperWindow(1);
				break;
			case(1):
				window.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
				window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
				break;
		}
	}
}