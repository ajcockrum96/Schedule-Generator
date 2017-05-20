package purduecrsinfo;
import schedulegeneration.*;
import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.*;
import java.util.ArrayList;

public class PurdueCourseParser {
	public ArrayList<SGCourseTime> courseOptions;

	public PurdueCourseParser(String htmlFilePath) {
		try {
			courseOptions = new ArrayList<SGCourseTime>();
			String courseName = htmlFilePath.substring(htmlFilePath.lastIndexOf(File.separatorChar) + 1, htmlFilePath.lastIndexOf('.'));
			File input = new File(htmlFilePath);
			Document doc = Jsoup.parse(input, "UTF-8", "");
			Element body = doc.getElementsByClass("pagebodydiv").first();
			Element bodyTable = body.getElementsByTag("table").first();
			Element bodyTableBody = bodyTable.getElementsByTag("tbody").first();
			Elements sections = bodyTableBody.getElementsByTag("tr");
			for(int i = 1; i < sections.size(); i += 2) {
				Element dddefault = sections.get(i).getElementsByTag("td").first();
				if(dddefault != null) {
					Element optionTable = dddefault.getElementsByTag("table").first();
					if(optionTable != null) {
						Element classBody = optionTable.getElementsByTag("tbody").first();
						Element classInfo = classBody.getElementsByTag("tr").get(1);
						Elements classData = classInfo.getElementsByTag("td");
						String courseNameMod = courseName + " " + classData.get(classData.size() - 2).text().trim();
						courseOptions.add(new SGCourseTime(classData.get(1).text(), classData.get(2).text(), courseNameMod));
					}
				}
			}
		} catch(IOException e) {
			System.err.format("%s\n", e);
		}
	}

	public ArrayList<String> generateOutputStrings() {
		String currCourse = "";
		int    currIndex  = -1;
		ArrayList<String> outputStrings = new ArrayList<String>();
		for(int i = 0; i < courseOptions.size(); ++i) {
			SGCourseTime currOption = courseOptions.get(i);
			if(!(currOption.courseName.equals(currCourse))) {
				currCourse = currOption.courseName;
				if(outputStrings.indexOf("1)\t" + currCourse) == -1) {
					outputStrings.add("1)\t" + currCourse);
				}
				currIndex = outputStrings.indexOf("1)\t" + currCourse) + 1;
			}
			if(currIndex < outputStrings.size()) {
				outputStrings.add(currIndex, String.format("%s\t%s", currOption.timePeriod.getDays(), currOption.timePeriod.rangeString()));
			}
			else {
				outputStrings.add(String.format("%s\t%s", currOption.timePeriod.getDays(), currOption.timePeriod.rangeString()));
			}
		}
		return outputStrings;
	}
}