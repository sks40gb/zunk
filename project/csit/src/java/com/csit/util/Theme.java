/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.util;

/**
 *
 * @author sunil
 */
public class Theme {
    public static final String CLASSICAL = "classical";
    public static final String GREEN = "green";
    public static final String INDIGO = "indigo";
    public static final String PURPLE = "purple";
    public static final String WATER = "water";
    public static final String DEFAULT_THEME = "classical";
    public static String curentTheme;    

    public static String getCurentTheme() {
         if(curentTheme == null || curentTheme.trim().equals("")){
            curentTheme = DEFAULT_THEME;
        }
        return curentTheme + ".css";
    }

    public static void setCurentTheme(String curentTheme) {       
        Theme.curentTheme = curentTheme;
    }  
}
