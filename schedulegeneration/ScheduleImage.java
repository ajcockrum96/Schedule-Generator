package schedulegeneration;

// import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;

// import java.awt.image.*;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;

// import java.io.*;
import java.io.IOException;
import java.io.File;

// import java.util.*;
import java.util.ArrayList;

// import javax.imageio.*;
import javax.imageio.ImageIO;

public class ScheduleImage {
	BufferedImage image;
	static int []BLACK     = {  0,   0,   0, 255};
	static int []DARKGRAY  = { 63,  63,  63, 255};
	static int []GRAY      = {127, 127, 127, 255};
	static int []LIGHTGRAY = {191, 191, 191, 255};
	static int []WHITE     = {255, 255, 255, 255};

	static int []RED       = {255,   0,   0, 255};
	static int []GREEN     = {  0, 255,   0, 255};
	static int []BLUE      = {  0,   0, 255, 255};

	static int []YELLOW    = {255, 255,   0, 255};
	static int []CYAN      = {  0, 255, 255, 255};
	static int []MAGENTA   = {255,   0, 255, 255};

	static int []ORANGE    = {255, 127,   0, 255};
	static int []TURQUOISE = {  0, 255, 127, 255};
	static int []PURPLE    = {127,   0, 255, 255};

	static int []PUKE      = {127, 255,   0, 255};
	static int []COBALT    = {  0, 127, 255, 255};
	static int []CRIMSON   = {255,   0, 127, 255};

	static int    MAX_LENGTH          = 18;			// Max of 4 Subject Chars, 5 Class Numerals, 7 Qualifier Chars (i.e. Lecture, Lab, Seminar) and 2 spaces
	static double PT_PX_CONVERSION    = 3.0 / 4.0;	// Conversion from Font Points to Pixels
	static double FONT_POS_CONVERSION = (1.0 + PT_PX_CONVERSION) / 2.0;	// Position needed to put 1 pt font vertically centered on 1 pixel

	public ScheduleImage(Schedule schedule) {
		int [][]colors = {
			this.BLACK,
			this.RED,
			this.GREEN,
			this.BLUE,
			this.YELLOW,
			this.CYAN,
			this.MAGENTA,
			this.ORANGE,
			this.TURQUOISE,
			this.PURPLE,
			this.PUKE,
			this.COBALT,
			this.CRIMSON,
			this.DARKGRAY,
			this.GRAY,
			this.LIGHTGRAY,
			this.WHITE
		};
		int scale       = 2;
		int hScale      = 15 * scale;
		int vScale      = 5 * scale;
		int imageWidth  = hScale * schedule.days.length();
		int imageHeight = vScale * schedule.numPeriods;
		int numBands    = 4;	// Number of data banks in buffer; one for each part of pixel type ABGR

		// Create Buffer, Model, and Image
		DataBufferInt    buffer = new DataBufferInt(hScale * vScale, numBands);
		BandedSampleModel model = new BandedSampleModel(DataBuffer.TYPE_INT, hScale, vScale, numBands);
		             this.image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);
		Point point = new Point(0, 0);
		for(int i = 0; i < schedule.days.length(); ++i) {
			point.x = i * hScale;
			for(int j = 0; j < schedule.numPeriods; ++j) {
				for(int x = 0; x < hScale; ++x) {
					for(int y = 0; y < vScale; ++y) {
						model.setPixel(x, y, colors[schedule.schedule.get(i).get(j)], buffer);
					}
				}
				point.y = j * vScale;
				this.image.setData(Raster.createRaster(model, buffer, point));
			}
		}
	}

	static public void writeImageFile(ScheduleImage scheduleImage, String filename) {
		try {
			File output = new File(filename);
			String fileType = filename.substring(filename.lastIndexOf('.') + 1);
			ImageIO.write(scheduleImage.image, fileType, output);
		} catch (IOException e) {
			System.err.format("%s%n", e);
		}
	}

	static public void writeImageKey(String filename, ArrayList<ClassInfo> classes) {
		int [][]colors = {
			BLACK,
			RED,
			GREEN,
			BLUE,
			YELLOW,
			CYAN,
			MAGENTA,
			ORANGE,
			TURQUOISE,
			PURPLE,
			PUKE,
			COBALT,
			CRIMSON,
			DARKGRAY,
			GRAY,
			LIGHTGRAY,
			WHITE
		};
		int hScale      = 250;
		int vScale      = 100;
		int imageWidth  = hScale * 5;	// FIXME
		int imageHeight = vScale * classes.size();
		int numBands    = 4;	// Number of data banks in buffer; one for each part of pixel type ABGR

		// Create Buffer, Model, and Image
		DataBufferInt    buffer = new DataBufferInt(hScale * vScale * classes.size(), numBands);
		BandedSampleModel model = new BandedSampleModel(DataBuffer.TYPE_INT, hScale, vScale * classes.size(), numBands);
		BufferedImage     image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

		// "Paint" the Colors used in the Schedules
		for(int i = 0; i < hScale; ++i) {
			for(int j = 0; j < classes.size() * vScale; ++j) {
				model.setPixel(i, j, colors[classes.get(j / vScale).number], buffer);
			}
		}
		Point point = new Point(0, 0);
		image.setData(Raster.createRaster(model, buffer, point));

		// Make the rest of the Image Black to start
		for(int i = 0; i < hScale; ++i) {
			for(int j = 0; j < classes.size() * vScale; ++j) {
				model.setPixel(i, j, BLACK, buffer);
			}
		}
		for(int i = 1; i < imageWidth / hScale; ++i) {
			point.x = i * hScale;
			image.setData(Raster.createRaster(model, buffer, point));
		}

		// Convert to Graphics and add Class Names
		Graphics2D image2D = image.createGraphics();
		image2D.setColor(Color.white);
		image2D.setFont(new Font("Arial", Font.ITALIC, vScale));
		for(int i = 0; i < classes.size(); ++i) {
			image2D.drawString(classes.get(i).name, hScale, (int)(vScale * FONT_POS_CONVERSION) + i * vScale);
		}

		try {
			File output = new File(filename);
			String fileType = filename.substring(filename.lastIndexOf('.') + 1);
			ImageIO.write(image, fileType, output);
		} catch (IOException e) {
			System.err.format("%s%n", e);
		}
	}
}