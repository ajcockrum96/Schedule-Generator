import javax.swing.*;
import java.lang.Thread;

public class JComboTesting {
	public static void main( String []args ) {
		JFrame win = new JFrame();
		win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		win.setSize( 700, 300 );
		String []testList = {"MA", "ECE", "AAE", "BAND"};
		JComboBox testBox = new JComboBox(testList);
		testBox.setEditable( true );
		win.add(testBox);
		win.setVisible( true );
		while(true) {
			System.out.format("%s\n", testBox.getSelectedItem());
			try {
				Thread.sleep(1000);
			} catch(Exception e) {
			}
		}
	}
}