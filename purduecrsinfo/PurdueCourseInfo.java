/*
 * © 2016-2017 Caleb Tung and Alexander Cockrum, all rights reserved. 
 */
package purduecrsinfo;

import java.io.*;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class PurdueCourseInfo {
    
    // Session terms to be used for passing as args to constructor
    public static final String FALL = "10";
    public static final String SPRING = "20";
    public static final String SUMMER = "30";
    
    // Constants used for building POST request body
    private final String _TERM_KEY = "term_in=";
    private final String _SUBJECT_KEY = "&sel_subj=";
    private final String _CRSNUM_KEY = "&sel_crse=";
    private final String _BTWN_TERM_SUBJ = "&sel_subj=dummy&sel_day=dummy&sel_schd=dummy&sel_insm=dummy&sel_camp=dummy&sel_levl=dummy&sel_sess=dummy&sel_instr=dummy&sel_ptrm=dummy&sel_attr=dummy";
    private final String _AFTER_CRSNUM = "&sel_title=&sel_schd=%25&sel_insm=%25&sel_from_cred=&sel_to_cred=&sel_camp=%25&sel_ptrm=%25&sel_instr=%25&sel_sess=%25&sel_attr=%25&begin_hh=0&begin_mi=0&begin_ap=a&end_hh=0&end_mi=0&end_ap=a";
    
    // URL of the form
    private final String _COURSE_FORM_URL = "https://selfservice.mypurdue.purdue.edu/prod/bwckschd.p_get_crse_unsec";
    
    // Referer URL used for passing server-side verification
    private final String _REFERER_URL = "https://selfservice.mypurdue.purdue.edu/";
    
    private String requestBody;
    
    // Standard OK HTTP code
    private final int _HTTP_OK = 200;
    
    // Fields with getters
    private String term;
    private String subject;
    private String courseNumber;
    private String rawHtml;
    
    /******** CONSTRUCTORS ********/
    public PurdueCourseInfo(int year, String session, String subject, String courseNumber)
    {
        if (session.equals(FALL))
        {
            year++; // accommodate Purdue's funky term numbering scheme
        }
        
        this.term = String.valueOf(year) + session;
        this.subject = subject;
        this.courseNumber = courseNumber;
        
        _buildRequestBody();
    }
    
    /******** GETTERS ********/
    public String getTerm() {
        return term;
    }

    public String getSubject() {
        return subject;
    }

    public String getCourseNumber() {
        return courseNumber;
    }
    
    public String getRawHtml() {
        return rawHtml;
    }
    
    /******** API METHODS ********/
    
     // @return HTTP Response code
     // @throws java.lang.Exception 
     // NOTE: This method will connect the PurdueCourseInfo to the Internet
    public int acquireHtmlFromInternet() throws Exception
    {           
        URL formUrl = new URL(_COURSE_FORM_URL);
        HttpsURLConnection conn = (HttpsURLConnection) formUrl.openConnection();
        
        // Build basic POST request
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Referer", _REFERER_URL);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        conn.setRequestProperty("Connection", "keep-alive");
        conn.setDoOutput(true);
        
        // Write request body
        try (DataOutputStream netStream = new DataOutputStream(conn.getOutputStream()))
        {
            netStream.writeBytes(requestBody);
            netStream.close();
        }
        
        int responseCode = conn.getResponseCode();
        
        if (responseCode == _HTTP_OK)
        {
            _fetchRawHtml(conn);
        }
        
        return responseCode;
    }
    
    /******** HELPER METHODS ********/
    private void _buildRequestBody()
    {
        requestBody = _TERM_KEY + term
                    + _BTWN_TERM_SUBJ // assign required dummy parameters
                    + _SUBJECT_KEY + subject
                    + _CRSNUM_KEY + courseNumber
                    + _AFTER_CRSNUM; // assign required dummy parameters
    }
    
    private void _fetchRawHtml(HttpsURLConnection conn) throws Exception
    {   
        try (BufferedReader inRead = new BufferedReader(new InputStreamReader(conn.getInputStream())))
        {
            String readLine;
            StringBuilder data = new StringBuilder();

            while ((readLine = inRead.readLine()) != null) {
                data.append(readLine);
            }
            
            inRead.close();
            
            rawHtml =  data.toString();
        }
    }
}
