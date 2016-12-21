/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
import java.util.Scanner;

/**
 *
 * @author caleb
 */
public class TestRun {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception 
    {
        System.out.println("DEMO: Dump info for Fall 2016 ECE 201 to an HTML File.");
        PurdueCourseInfo info = new PurdueCourseInfo(2016, PurdueCourseInfo.FALL, "ECE", "201");
        System.out.println("HTTP Response Code: " + info.acquireHtmlFromInternet());
        
        System.out.println("Enter target HTML dump filename:");
        Scanner sc = new Scanner(System.in);
        String fileName = sc.nextLine();
        sc.close();
        FileWriter fw = new FileWriter(fileName);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(info.getRawHtml());
        bw.close();
        System.out.println("Raw HTML dumped to file: " + fileName);
    }
}
