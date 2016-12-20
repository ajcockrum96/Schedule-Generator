import schedulegeneration.*;

public class SGRun {
	public static void main( String []args ) {
		try {
			SGWindow win = new SGWindow("rawInput.txt", "preferredInput.txt");
		} catch (Exception e) {
			System.err.format("%s%n", e);
		}
	}
}