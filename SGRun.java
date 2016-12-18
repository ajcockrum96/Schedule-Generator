// import javax.swing.*;

// import java.awt.*;

// import java.io.*;

// import java.util.*;

public class SGRun {
	public static void main ( String []args ) {
		try {
			SGWindow win = new SGWindow();
		} catch (Exception e) {
			System.err.format("%s%n", e);
		}
	}
}