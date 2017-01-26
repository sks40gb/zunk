package com.core.date;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author sunil
 */
public class CurrentDate {
    
    public static void main(String[] args) {
        Timestamp dTime = new Timestamp(new java.util.Date().getTime());
        
        //2008-07-18 11:53:36.258
        
        Calendar cal = new GregorianCalendar();
        
        
        SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd:hh_mm");
        DateFormat _format = new DateFormat();
        
        String strDate = format.format(new Date());        
        
        System.out.println("-------------> " + strDate);
        
        
    }

}
