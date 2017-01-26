/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author gprakash
 */
/**class used to manipulate the date */
public class DataConversion {

    /**Get the date in long 
     * @param dateInStringFormat date as string        
     */
    public static long getDateInLongFormat(String dateInStringFormat) throws ParseException { 
        String format = "yyyy-dd-MM HH:mm:ss.SSS";
        DateFormat formatter = new SimpleDateFormat(format);
        Date date = (Date) formatter.parse(dateInStringFormat);
        Long longDate = date.getTime();
        return longDate;
    }
    
    /**Get the date in long 
     * @param dateInStringFormat date as string        
     * @param format date format
     */
    public static long getDateInLongFormat(String dateInStringFormat, String format) throws ParseException {  
        DateFormat formatter = new SimpleDateFormat(format);
        Date date = (Date) formatter.parse(dateInStringFormat);
        Long longDate = date.getTime();
        return longDate;
    }
    
     /**Get the difference of two date 
     * @param date1        
     * @param date2 
     */
    public static Long getDateDifference(Date date1, Date date2) throws ParseException{
        return getDateInLongFormat(date1.toString()) - getDateInLongFormat(date2.toString());
    }
    
    /**Get the difference of two date 
     * @param date1   date as string     
     * @param date2   date as string
     */
    public static Long getDateDifference(String date1, String date2) throws ParseException{
        return getDateInLongFormat(date1) - getDateInLongFormat(date2);
    }
    
    
}
