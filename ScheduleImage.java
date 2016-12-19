// import java.awt.*;
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

// import javax.imageio.*;
import javax.imageio.ImageIO;

public class ScheduleImage {
	BufferedImage image;
	int []BLACK     = {  0,   0,   0, 255};
	int []DARKGRAY  = { 63,  63,  63, 255};
	int []GRAY      = {127, 127, 127, 255};
	int []LIGHTGRAY = {191, 191, 191, 255};
	int []WHITE     = {255, 255, 255, 255};

	int []RED       = {255,   0,   0, 255};
	int []GREEN     = {  0, 255,   0, 255};
	int []BLUE      = {  0,   0, 255, 255};

	int []YELLOW    = {255, 255,   0, 255};
	int []CYAN      = {  0, 255, 255, 255};
	int []MAGENTA   = {255,   0, 255, 255};

	int []ORANGE    = {255, 127,   0, 255};
	int []TURQUOISE = {  0, 255, 127, 255};
	int []PURPLE    = {127,   0, 255, 255};

	int []PUKE      = {127, 255,   0, 255};
	int []COBALT    = {  0, 127, 255, 255};
	int []CRIMSON   = {255,   0, 127, 255};

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
		int hScale      = 15;
		int vScale      = 5;
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
			System.err.format("%e%n", e);
		}
	}
}