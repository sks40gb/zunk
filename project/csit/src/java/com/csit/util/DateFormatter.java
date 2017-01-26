/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author sunil
 */
public class DateFormatter {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    public static void main(String[] args) throws ParseException {

        Date date = convertStringToDate("2009-06-01");
        String s = convertDateToString(date,"EEEE, MMMM d, yyyy");
        System.out.println("==============> " + s);


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
       return convertDateToString(date, DATE_FORMAT);
    }

    public static String convertDateToString(Date date, String FORMAT) throws ParseException {
        if(date != null){
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT, new Locale("US_en"));
            return sdf.format(date);
        }else{
            return null;
        }
    }
}
