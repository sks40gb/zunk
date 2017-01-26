/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.date;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 * @author sunil
 */
public class NextNPreviousMonth {

    public static void main(String[] args) {
        Calendar cal = GregorianCalendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MMMM-yyyy");
        SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM");
        Date currentMonth = new Date();
        cal.setTime(currentMonth);
        // current month
        String currentMonthAsSting = df.format(cal.getTime());        
        // Add next month
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 6);
        for(int i = 0 ; i < 18 ; i++){
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
            cal.set(Calendar.DAY_OF_MONTH,1);
            String nextMonthAsString = df.format(cal.getTime());
            Date d = new Date(cal.getTime().getTime());

            System.out.println(df1.format(cal.getTime()) + "---- "+d+"--------" + nextMonthAsString);
        }

    }
}
