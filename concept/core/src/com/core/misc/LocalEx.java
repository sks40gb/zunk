package com.core.misc;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 1. we can format the simple way or by using the language or language and country
 * ex : No locale
 *    : Locale with Language
 *    : Language in country
 *    : ex : en in India and en in US
 *    : ex : It in US and so on
 * so summary is  - language used in the country
 * @author Sunil
 */
public class LocalEx {

    public static void main(String[] args) {
        Date date = new Date();
        Locale locale = new Locale("en");
        Locale localeIN = new Locale("en","in");
        Locale localeUS = new Locale("en","us");

        // for date formatting
        DateFormat dateFormat =  DateFormat.getDateInstance(DateFormat.FULL, locale);
        DateFormat dateFormatIN =  DateFormat.getDateInstance(DateFormat.FULL, localeIN);
        DateFormat dateFormatUS =  DateFormat.getDateInstance(DateFormat.FULL, localeUS);
        System.out.println("DATE-----------");
        System.out.println(dateFormat.format(date));
        System.out.println(dateFormatIN.format(date));
        System.out.println(dateFormatUS.format(date));

        // for currency
        double number = 500010d;
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        NumberFormat numberFormatIN = NumberFormat.getCurrencyInstance(localeIN);
        NumberFormat numberFormatUS = NumberFormat.getCurrencyInstance(localeUS);
        System.out.println("NUMBER-----------");
        System.out.println(numberFormat.format(number));
        System.out.println(numberFormatIN.format(number));
        System.out.println(numberFormatUS.format(number));
    }

}
