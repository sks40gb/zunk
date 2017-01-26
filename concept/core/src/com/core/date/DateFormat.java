package com.core.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author sunil
 */
public class DateFormat {

    public static void main(String[] args) {
       String d = "2009-12-10 18:57:16.0";

       Date date = null;
       SimpleDateFormat format  = new SimpleDateFormat("yyyy-MM-dd ss:mm:HH.s");
        try {
            date = format.parse(d);
        } catch (ParseException ex) {
            date = new Date();
        }

       SimpleDateFormat df = new SimpleDateFormat("MMMM dd, yyyy");
       //June 9, 2009
      // Date date = new Date();       
       System.out.println(date + "===================" + df.format(date));
    }
    
}
