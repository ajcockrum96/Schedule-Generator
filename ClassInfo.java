// import java.awt.*;

// import java.io.*;

// import java.util.*;
import java.util.ArrayList;


public class ClassInfo {
	String name;
	int number;

	public ClassInfo(String className, int classNum) {
		name = className;
		number = classNum;
	}

	public ClassInfo(ClassTime classTime, int classNum) {
		this(classTime.className, classNum);
	}

	// BUBBLE SORT INEFFICIENT; REDO WHEN POSSIBLE
	static public ArrayList<ClassInfo> sortClassInfoArrayList(ArrayList<ClassInfo> classes) {
		if(classes != null) {
			for(int i = 0; i < classes.size() - 1; ++i) {
				for(int j = i + 1; j < classes.size(); ++j) {
					if(classes.get(i).number > classes.get(j).number) {
						ClassInfo a = classes.get(i);
						ClassInfo b = classes.get(j);
						classes.remove(i);
						classes.remove(j - 1);
						classes.add(i, b);
						classes.add(j, a);
					}
				}
			}
		}
		return classes;
	}

	// Merge Sort Implementation
	static public ArrayList<ClassInfo> mergeSortClassInfoArrayList(ArrayList<ClassInfo> classes, int startIndex, int endIndex) {
		if(classes != null && startIndex >= 0 && endIndex <= classes.size()) {
			int length = endIndex - startIndex;
			if(length > 2) {
				int midIndex = length / 2 + startIndex;
				classes = mergeSortClassInfoArrayList(classes, startIndex, midIndex);
				classes = mergeSortClassInfoArrayList(classes, midIndex, endIndex);
				int i, j;
				for(i = startIndex, j = midIndex; i < midIndex && j < endIndex;) {
					if(classes.get(i).number < classes.get(j).number) {
						ClassInfo temp = classes.remove(i);
						classes.add(startIndex++, temp);
						++i;
					}
					else {
						ClassInfo temp = classes.remove(j);
						classes.add(startIndex++, temp);
						++midIndex;
						++i;
						++j;
					}
				}
				for(; i < midIndex; ++i) {
						ClassInfo temp = classes.remove(i);
						classes.add(startIndex++, temp);
				}
				for(; j < endIndex; ++j) {
						ClassInfo temp = classes.remove(j);
						classes.add(startIndex++, temp);
				}
			}
			else if(length == 2) {
				if(classes.get(startIndex).number > classes.get(startIndex + 1).number) {
						ClassInfo a = classes.get(startIndex);
						ClassInfo b = classes.get(startIndex + 1);
						classes.remove(startIndex);
						classes.remove(startIndex);
						classes.add(startIndex, b);
						classes.add(startIndex + 1, a);
				}
			}
		}
		return classes;
	}

	static public int searchClassInfoArrayList(ArrayList<ClassInfo> classes, String className, int classNum) {
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