package schedulegeneration;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BandedSampleModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * <p>
 * A buffered image wrapper that stores an image representation of a Schedule
 * instances 2D schedule representation.
 *
 * This class has two main methods that each write an image to a file. One
 * requires a SGImage instance because it writes the image contained
 * in the instance to a given filename. The other generates a key for the
 * schedules that would be generated by a given list of SGCourseInfo objects
 * so that the user can quickly and easily read the SGImage image files.
 * </p>
 * @see Schedule
 * @see ScheduleGenerator
 */
public class SGImage {
	/**
	 * Buffered Image of this SGImage
	 */
	public BufferedImage image;

	/**
	 * ABGR representation of the Color Black
	 */
	public static int []BLACK     = {  0,   0,   0, 255};

	/**
	 * ABGR representation of the Color Dark Gray
	 */
	public static int []DARKGRAY  = { 63,  63,  63, 255};

	/**
	 * ABGR representation of the Color Gray
	 */
	public static int []GRAY      = {127, 127, 127, 255};

	/**
	 * ABGR representation of the Color Light Gray
	 */
	public static int []LIGHTGRAY = {191, 191, 191, 255};

	/**
	 * ABGR representation of the Color White
	 */
	public static int []WHITE     = {255, 255, 255, 255};

	/**
	 * ABGR representation of the Color Red
	 */
	public static int []RED       = {255,   0,   0, 255};

	/**
	 * ABGR representation of the Color Green
	 */
	public static int []GREEN     = {  0, 255,   0, 255};

	/**
	 * ABGR representation of the Color Blue
	 */
	public static int []BLUE      = {  0,   0, 255, 255};

	/**
	 * ABGR representation of the Color Yellow
	 */
	public static int []YELLOW    = {255, 255,   0, 255};

	/**
	 * ABGR representation of the Color Cyan
	 */
	public static int []CYAN      = {  0, 255, 255, 255};

	/**
	 * ABGR representation of the Color Magenta
	 */
	public static int []MAGENTA   = {255,   0, 255, 255};

	/**
	 * ABGR representation of the Color Orange
	 */
	public static int []ORANGE    = {255, 127,   0, 255};

	/**
	 * ABGR representation of the Color Turquoise
	 */
	public static int []TURQUOISE = {  0, 255, 127, 255};

	/**
	 * ABGR representation of the Color Purple
	 */
	public static int []PURPLE    = {127,   0, 255, 255};

	/**
	 * ABGR representation of the Color Puke
	 */
	public static int []PUKE      = {127, 255,   0, 255};

	/**
	 * ABGR representation of the Color Cobalt
	 */
	public static int []COBALT    = {  0, 127, 255, 255};

	/**
	 * ABGR representation of the Color Crimson
	 */
	public static int []CRIMSON   = {255,   0, 127, 255};

	/**
	 * <p>
	 * Maximum expected length of a course name.
	 *
	 * Based off of a maximum of:
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;4 Subject Characters
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;5 Course Numerals
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;7 Qualifier Characters (For example: Lecture, Lab, Seminar...)
	 * <br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;2 Space Characters
	 * </p>
	 */
	static int    MAX_LENGTH          = 18;

	/**
	 * Conversion from Points (font size) to Pixels
	 */
	static double PT_PX_CONVERSION    = 3.0 / 4.0;

	/**
	 * Position needed to vertically center a 1 pt font on 1 pixel
	 */
	static double FONT_POS_CONVERSION = (1.0 + PT_PX_CONVERSION) / 2.0;

	/**
	 * <p>
	 * Constructs a SGImage for a given Schedule.
	 *
	 * This creates an image representing the 2D grid representation given
	 * by the {@link Schedule#schedule} static field.
	 * </p>
	 * <p>
	 * This method first uses a DataBuffer and Raster to "paint" each slot in
	 * the grid based on the integer value in the given schedule representation.
	 * These values are representative of the course number of the SGCourseInfo
	 * object that is using the given time slot. The value 0 means empty and
	 * thus uses the color black, stored at index 0 of the colors array. The
	 * remaining values the colors at the respective indices in the colors
	 * array.
	 * </p>
	 *
	 * @param schedule			the Schedule instance to use
	 */
	public SGImage(Schedule schedule) {
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

	/**
	 * Writes the image of a given SGImage instance to a given filename.
	 *
	 * @param scheduleImage		the SGImage instance to write to a file
	 * @param filename			the String of the filename to write
	 */
	static public void writeImageFile(SGImage scheduleImage, String filename) {
		try {
			File output = new File(filename);
			String fileType = filename.substring(filename.lastIndexOf('.') + 1);
			ImageIO.write(scheduleImage.image, fileType, output);
		} catch (IOException e) {
			System.err.format("%s%n", e);
		}
	}

	/**
	 * <p>
	 * Writes an image key of a given ArrayList of SGCourseInfo objects to a given filename.
	 *
	 * This method first uses a DataBuffer and Raster to "paint" blocks of color
	 * for each course, in the order of the given ArrayList, with the colors
	 * corresponding to their respective {@link SGCourseInfo#number} static fields,
	 * like the SGImage constructor.
	 * </p>
	 * <p>
	 * After painting these blocks of colors, the BufferedImage is converted
	 * to a Graphics2D object so that text can be painted on as well. The font
	 * size is based on the vertical scale, so that it will fit in the height
	 * of the block of color. Additionally, the image width is based on the font
	 * size and the {@link MAX_LENGTH} field so that the text does not overflow.
	 * The text for each course is appended in a similar fashion to the image,
	 * using drawString(...) from the Graphics2D class.
	 * </p>
	 * <p>
	 * Finally, the image is written to the given filename.
	 * </p>
	 *
	 * @param filename			the String of the filename to write
	 * @param courses			the ArrayList of SGCourseInfo objects to construct a key from
	 */
	static public void writeImageKey(String filename, ArrayList<SGCourseInfo> courses) {
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
		int vScale      = hScale * 2 / 5;
		int imageWidth  = hScale * 5;	// Corresponds to MAX_LENGTH to avoid overflow
		int imageHeight = vScale * courses.size();
		int numBands    = 4;			// Number of data banks in buffer; one for each part of pixel type ABGR

		// Create Buffer, Model, and Image
		DataBufferInt    buffer = new DataBufferInt(hScale * vScale * courses.size(), numBands);
		BandedSampleModel model = new BandedSampleModel(DataBuffer.TYPE_INT, hScale, vScale * courses.size(), numBands);
		BufferedImage     image = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_4BYTE_ABGR);

		// "Paint" the Colors used in the Schedules
		for(int i = 0; i < hScale; ++i) {
			for(int j = 0; j < courses.size() * vScale; ++j) {
				model.setPixel(i, j, colors[courses.get(j / vScale).number], buffer);
			}
		}
		Point point = new Point(0, 0);
		image.setData(Raster.createRaster(model, buffer, point));

		// Make the rest of the Image Black to start
		for(int i = 0; i < hScale; ++i) {
			for(int j = 0; j < courses.size() * vScale; ++j) {
				model.setPixel(i, j, BLACK, buffer);
			}
		}
		for(int i = 1; i < imageWidth / hScale; ++i) {
			point.x = i * hScale;
			image.setData(Raster.createRaster(model, buffer, point));
		}

		// Convert to Graphics and add Course Names
		Graphics2D image2D = image.createGraphics();
		image2D.setColor(Color.white);
		image2D.setFont(new Font("Arial", Font.ITALIC, vScale));
		for(int i = 0; i < courses.size(); ++i) {
			image2D.drawString(courses.get(i).name, hScale, (int)(vScale * FONT_POS_CONVERSION) + i * vScale);
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