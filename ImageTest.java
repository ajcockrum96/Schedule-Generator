import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;

public class ImageTest {
	public static void main( String []args) {
		JFrame win = new JFrame("Image Test");
		win.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		win.setResizable( false );
		win.setSize( 500, 500 );

		// Create DataBuffer
		DataBufferInt buffer = new DataBufferInt(32);
		
		// Create SampleModel
		SampleModel sampleModel = new SampleModel(DataBuffer.TYPE_INT, 60, 15, 3);

		// Create Raster

		// Create Image


		win.setVisible( true );
	}
}