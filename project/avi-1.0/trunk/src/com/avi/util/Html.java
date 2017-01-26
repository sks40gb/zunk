/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.util;

/**
 *
 * @author Admin
 */
public class Html {

    private static final String ODD = "odd";
    private static final String EVEN = "even";
    public static String rowClassName;
    
    public static String getCssClassName(){
        if(EVEN.equals(rowClassName)){
          rowClassName = ODD;
          return EVEN;
        }else{
          rowClassName = EVEN;
          return ODD;
        }
    }

    public static String getSemesters() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 1; i <= 6; i++) {
            buffer.append("<option value='");
            buffer.append(i);
            buffer.append("'>");
            buffer.append("semester " + i);
            buffer.append("</option>");
        }
        return buffer.toString();
    }


    public static String getDayOptions() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 1; i <= 31; i++) {
            buffer.append("<option value='");
            buffer.append(i);
            buffer.append("'>");
            buffer.append(i);
            buffer.append("</option>");
        }
        return buffer.toString();
    }

    public static String getYearOptions() {
        StringBuffer buffer = new StringBuffer();
        for (int i = 1990; i <= 2009; i++) {
            buffer.append("<option value='");
            buffer.append(i);
            buffer.append("'>");
            buffer.append(i);
            buffer.append("</option>");
        }
        return buffer.toString();
    }

    public static String getMonthOptions() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<option value='01'>Jan</option>");
        buffer.append("<option value='02'>Feb</option>");
        buffer.append("<option value='03'>Mar</option>");
        buffer.append("<option value='04'>Apr</option>");
        buffer.append("<option value='05'>May</option>");
        buffer.append("<option value='06'>Jun</option>");
        buffer.append("<option value='07'>Jul</option>");
        buffer.append("<option value='08'>Aug</option>");
        buffer.append("<option value='09'>Sep</option>");
        buffer.append("<option value='10'>Oct</option>");
        buffer.append("<option value='11'>Nov</option>");
        buffer.append("<option value='12'>Dec</option>");
        return buffer.toString();
    }

    public static String getCountryOptions() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<option value='IN'>INDIA</option>");
        buffer.append("<option value='AU'>AUSTRALIA</option>");
        buffer.append("<option value='CA'>CANADA</option>");
        buffer.append("<option value='FI'>FINLAND</option>");
        buffer.append("<option value='FR'>FRANCE</option>");
        buffer.append("<option value='GF'>FRENCH GUIANA(TERRITORY)</option>");
        buffer.append("<option value='SZ'>SWITZERLAND</option>");
        buffer.append("<option value='ZM'>ZAMBIA</option>");
        buffer.append("<option value='MY'>MALAYSIA</option>");
        buffer.append("<option value='JP'>JAPAN</option>");
        buffer.append("<option value='IT'>ITALY</option>");
        buffer.append("<option value='IR'>IRAN</option>");
        buffer.append("<option value='ZW'>ZIMBABWE</option>");
        buffer.append("<option value='OT'>OTHER</option>");
        return buffer.toString();
    }
}
