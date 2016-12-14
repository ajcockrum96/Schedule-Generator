import java.awt.*;
import java.io.*;
import java.util.*;

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
		return classes;
	}

	static public int searchClassInfoArrayList(ArrayList<ClassInfo> classes, String className) {
		for(int i = 0; i < classes.size(); ++i) {
			if(classes.get(i).name.compareTo(className) == 0) {
				return i;
			}
		}
		return -1;
	}
}