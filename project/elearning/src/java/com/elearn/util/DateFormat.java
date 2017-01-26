/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.elearn.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author sunil
 */
public class DateFormat {

    public static String getCurrentDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd");
        Date date = new Date();
        String dateStr = format.format(date);
        return dateStr;
    }

    public static String getConvertedDate(String dd, String mm, String yyyy){
        String dt = yyyy + "-" + mm + "-" + dd;        
        return dt;
    }

}
