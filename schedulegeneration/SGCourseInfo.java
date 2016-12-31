package schedulegeneration;

import java.util.ArrayList;

/**
 * <p>
 * A basic Object type that stores a String and int value together.
 *
 * For this {@link schedulegeneration} package, it is applied to store a class name
 * and number for keeping track of which classes are already present in the
 * schedule being generated.
 * </p>
 * <p>
 * The name field typically contains a subject, number, and qualifier,
 * but is not required by this class to do so. The number can be used
 * for various listing and identification purposes.
 * </p>
 * <p>
 * Despite the simplicity of what the object contains, there are not many
 * methods included in this class, dealing only with merge sorting based on
 * the static field {@link number} and searching based on both fields.
 * </p>
 */
public class SGCourseInfo {
	/**
	 * Name associated with the Class
	 */
	public String name;

	/**
	 * Number associated with the class
	 */
	public int number;

	/**
	 * Constructs a {@link SGCourseInfo} Object with specified name and number.
	 *
	 * @param className		the String of the class name
	 * @param classNum		the number associated with this class instance
	 */
	public SGCourseInfo(String className, int classNum) {
		name = className;
		number = classNum;
	}

	/**
	 * Constructs a {@link SGCourseInfo} Object from the name of a specified
	 * {@link SGCourseTime} and number.
	 * 
	 * @param classTime		the SGCourseTime object containing the desired class name
	 * @param classNum		the number associated with this class instance
	 */
	public SGCourseInfo(SGCourseTime classTime, int classNum) {
		this(classTime.className, classNum);
	}

	/**
	 * Merge sorts an ArrayList of SGCourseInfo Objects based on number fields.
	 *
	 * @param classes		the ArrayList of SGCourseInfo objects to sort
	 * @param startIndex	the first index of the ArrayList that needs sorting
	 * @param endIndex		the last index (non-inclusive) of the ArrayList that needs sorting
	 */
	static public void mergeSortSGCourseInfoArrayList(ArrayList<SGCourseInfo> classes, int startIndex, int endIndex) {
		if(classes != null && startIndex >= 0 && endIndex <= classes.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				mergeSortSGCourseInfoArrayList(classes, startIndex, midIndex);
				mergeSortSGCourseInfoArrayList(classes, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					if(classes.get(i).number < classes.get(j).number) {
						SGCourseInfo temp = classes.remove(i);
						classes.add(startIndex++, temp);
						++i;
					}
					else {
						SGCourseInfo temp = classes.remove(j);
						classes.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
				}
				for(; i < midIndex; ++i) {
						SGCourseInfo temp = classes.remove(i);
						classes.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						SGCourseInfo temp = classes.remove(j);
						classes.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				if(classes.get(startIndex).number > classes.get(startIndex + 1).number) {
					SGCourseInfo a = classes.get(startIndex);
					SGCourseInfo b = classes.get(startIndex + 1);
					classes.remove(startIndex);
					classes.remove(startIndex);
					classes.add(startIndex, b);
					classes.add(startIndex + 1, a);
				}
			}
		}
	}

	/**
	 * Searches for a SGCourseInfo object based on both {@link name} and {@link number} fields.
	 *
	 * @param classes		the ArrayList of SGCourseInfo objects to search
	 * @param className		the String that the name field must match
	 * @param classNum		the value that the number field must match
	 * @return 				the index of the first matching SGCourseInfo Object
	 * 						returns -1 (FAILURE) if match not found
	 */
	static public int searchSGCourseInfoArrayList(ArrayList<SGCourseInfo> classes, String className, int classNum) {
		if(classes != null) {
			for(int i = 0; i < classes.size(); ++i) {
				if(classes.get(i).name.compareTo(className) == 0 && classes.get(i).number == classNum) {
					return i;
				}
			}
		}
		return -1;
	}
}