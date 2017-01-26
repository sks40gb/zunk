/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author shanmugam
 */
public class DateUtils {

    public static Date parseToDate(String date, String dateFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        Date parsedDate = null;
        if (null != date && !date.trim().equals("")) {
            parsedDate = (Date) sdf.parse(date.trim());
        }
        return parsedDate;
    }

    public static String formatToString(Date date, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        String formattedDate = null;       
        formattedDate = sdf.format(date);        
        return formattedDate;
    }
}
