import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.*;
import purduecrsinfo.PurdueCourseParser;
import java.util.ArrayList;

public class JSoupTest {
	public static void main( String []args ) {
		System.out.println("Test");
		File input = new File("." + File.separatorChar + "purduecrsinfo" + File.separatorChar + "ECE362.html");
		try {
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
						for(int j = 0; j < classData.size(); ++j) {
							System.out.format("%s\n", classData.get(j).text());
						}
						System.out.println("");
					}
				}
			}
			PurdueCourseParser test = new PurdueCourseParser("." + File.separatorChar + "purduecrsinfo" + File.separatorChar + "ECE362.html");
		} catch(IOException e) {
			System.err.format("%s\n", e);
		}
	}
}