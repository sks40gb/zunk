/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sunil
 */
public class Sample {

    public static void main(String s[]){

        // alphanumeric values
        //String regex = "[0-9a-zA-Z]*";          
        //String str = "ThIS is OK tO tESt";
        String regex = "\\w*";          
        String str = "ThIS $$_ the 123 is OK tO tESt";
        
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);

        if(m.find()){
            System.out.println(m.group());
        }

    }

}
