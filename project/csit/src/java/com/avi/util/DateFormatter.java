/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author sunil
 */
public class DateFormatter {

    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd ss:mm:HH";

    public static void main(String[] args) {
        java.sql.Date date = new java.sql.Date(new Date().getTime());
        Date date1 = new Date(date.getTime());
        java.sql.Date date2 = new java.sql.Date(date1.getTime());
        System.out.println(date1 + " <=====> " + date + "<=====> " +  date2);

    }

    public static String getCurrentDate() {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Date date = new Date();
        return format.format(date);
    }

    public static boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (java.text.ParseException e) {

            return false;
        }
        if (!sdf.format(testDate).equals(date)) {

            return false;
        }
        return true;
    }

    public static Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        Date date = sdf.parse(dateString);
        return date;
    }
    public static String convertDateToString(Date date) throws ParseException {
        if(date != null){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.format(date);
        }else{
            return null;
        }
    }
}
