package com.core.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sunil
 */
public class CaseInsesitive {
    
    String getText(){
      return "text";   
    }
    
    public static void main(String[] args) {
        
        String txt = "SUnilxxsunIl";
        //(?i) will search the String sunil. It makes it Case Insesitive.
        //(?-i) will make it case sensitive.
        String patt = "(?i)su(?-i)nil";
        
        Pattern p = Pattern.compile(patt);
        Matcher m = p.matcher(txt);
        
        while(m.find())
        {
            System.out.println("---------------------> " + m.group());
        }
    }

}
