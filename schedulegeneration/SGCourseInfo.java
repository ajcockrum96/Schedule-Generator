package schedulegeneration;

import java.util.ArrayList;

/**
 * <p>
 * A basic Object type that stores a String and int value together.
 *
 * For this {@link schedulegeneration} package, it is applied to store a course name
 * and number for keeping track of which courses are already present in the
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
	 * Name associated with the Course
	 */
	public String name;

	/**
	 * Number associated with the course
	 */
	public int number;

	/**
	 * Constructs a {@link SGCourseInfo} Object with specified name and number.
	 *
	 * @param courseName		the String of the course name
	 * @param courseNum			the number associated with this class instance
	 */
	public SGCourseInfo(String courseName, int courseNum) {
		name = courseName;
		number = courseNum;
	}

	/**
	 * Constructs a {@link SGCourseInfo} Object from the name of a specified
	 * {@link SGCourseTime} and number.
	 * 
	 * @param courseTime	the SGCourseTime object containing the desired course name
	 * @param courseNum		the number associated with this class instance
	 */
	public SGCourseInfo(SGCourseTime courseTime, int courseNum) {
		this(courseTime.courseName, courseNum);
	}

	/**
	 * Merge sorts an ArrayList of SGCourseInfo Objects based on number fields.
	 *
	 * @param courses		the ArrayList of SGCourseInfo objects to sort
	 * @param startIndex	the first index of the ArrayList that needs sorting
	 * @param endIndex		the last index (non-inclusive) of the ArrayList that needs sorting
	 */
	static public void mergeSortSGCourseInfoArrayList(ArrayList<SGCourseInfo> courses, int startIndex, int endIndex) {
		if(courses != null && startIndex >= 0 && endIndex <= courses.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				mergeSortSGCourseInfoArrayList(courses, startIndex, midIndex);
				mergeSortSGCourseInfoArrayList(courses, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					if(courses.get(i).number < courses.get(j).number) {
						SGCourseInfo temp = courses.remove(i);
						courses.add(startIndex++, temp);
						++i;
					}
					else {
						SGCourseInfo temp = courses.remove(j);
						courses.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
				}
				for(; i < midIndex; ++i) {
						SGCourseInfo temp = courses.remove(i);
						courses.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						SGCourseInfo temp = courses.remove(j);
						courses.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				if(courses.get(startIndex).number > courses.get(startIndex + 1).number) {
					SGCourseInfo a = courses.get(startIndex);
					SGCourseInfo b = courses.get(startIndex + 1);
					courses.remove(startIndex);
					courses.remove(startIndex);
					courses.add(startIndex, b);
					courses.add(startIndex + 1, a);
				}
			}
		}
	}

	/**
	 * Searches for a SGCourseInfo object based on both {@link name} and {@link number} fields.
	 *
	 * @param courses		the ArrayList of SGCourseInfo objects to search
	 * @param courseName	the String that the name field must match
	 * @param courseNum		the value that the number field must match
	 * @return 				the index of the first matching SGCourseInfo Object
	 * 						returns -1 (FAILURE) if match not found
	 */
	static public int searchSGCourseInfoArrayList(ArrayList<SGCourseInfo> courses, String courseName, int courseNum) {
		if(courses != null) {
			for(int i = 0; i < courses.size(); ++i) {
				if(courses.get(i).name.compareTo(courseName) == 0 && courses.get(i).number == courseNum) {
					return i;
				}
			}
		}
		return -1;
	}
}