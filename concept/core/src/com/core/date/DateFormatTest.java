/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
public class DateFormatTest {
    public static void main( String[] args ) 
    {
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        DateFormat localFormat = DateFormat.getDateInstance();
        DateFormat format1 = new SimpleDateFormat( "yyyyMMdd" );
        DateFormat format2 = new SimpleDateFormat( "yyyy.MM.dd" );
        DateFormat format3 = new SimpleDateFormat( "MM/dd/yyyy" );
        DateFormat format4 = new SimpleDateFormat( "MM-dd-yyyy" );
        DateFormat format5 = new SimpleDateFormat( "dd/MM/yyyy" );
        DateFormat format6 = new SimpleDateFormat( "yyyyMMdd" );        
        System.out.println( "Localized  " + localFormat.format( date ) );
        System.out.println( "yyyyMMdd   " + format1.format( date ) );
        System.out.println( "yyyy.MM.dd " + format2.format( date ) );
        System.out.println( "MM/dd/yyyy " + format3.format( date ) );
        System.out.println( "MM-dd-yyyy " + format4.format( date ) );
        System.out.println( "dd/MM/yyyy " + format5.format( date ) );
        System.out.println( "yyyyMMdd " + format6.format( date ) );
    }
}


