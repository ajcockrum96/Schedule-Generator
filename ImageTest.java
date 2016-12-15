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

		int imageWidth  = 400;
		int imageHeight = 400;
		int hBlock      = imageWidth  / 8;
		int vBlock      = imageHeight / 8;

		// Create DataBuffer
		DataBufferInt buffer = new DataBufferInt(imageWidth * imageHeight, 4);
		
		// Create SampleModel
		BandedSampleModel model = new BandedSampleModel(DataBuffer.TYPE_INT, imageWidth, imageHeight, 4);
		int []blackPixel   = {  0,   0,   0, 255};
		int []redPixel     = {255,   0,   0, 255};
		int []yellowPixel  = {255, 255,   0, 255};
		int []greenPixel   = {  0, 255,   0, 255};
		int []cyanPixel    = {  0, 255, 255, 255};
		int []bluePixel    = {  0,   0, 255, 255};
		int []magentaPixel = {255,   0, 255, 255};
		int []whitePixel   = {255, 255, 255, 255};
		for(int i = 0; i < imageWidth; ++i) {
			for(int j = 0; j < imageHeight; ++j) {
				switch(i / hBlock % 8 + j / vBlock % 8) {
					case(0):
					case(14):
						model.setPixel(i, j, blackPixel, buffer);
						break;
					case(1):
					case(13):
						model.setPixel(i, j, redPixel, buffer);
						break;
					case(2):
					case(12):
						model.setPixel(i, j, yellowPixel, buffer);
						break;
					case(3):
					case(11):
						model.setPixel(i, j, greenPixel, buffer);
						break;
					case(4):
					case(10):
						model.setPixel(i, j, cyanPixel, buffer);
						break;
					case(5):
					case(9):
						model.setPixel(i, j, bluePixel, buffer);
						break;
					case(6):
					case(8):
						model.setPixel(i, j, magentaPixel, buffer);
						break;
					case(7):
						model.setPixel(i, j, whitePixel, buffer);
						break;
				}
			}
		}

		// Create Point
		Point origin = new Point(0, 0);

		// Create Raster
		Raster raster = Raster.createRaster(model, buffer, point);

		// Create Image
		BufferedImage image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
		image.setData(raster);

		win.add(new JLabel(new ImageIcon(image)));
		win.setVisible( true );
	}
}