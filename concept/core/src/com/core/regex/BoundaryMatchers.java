package com.core.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * To check if a pattern begins and ends on a word boundary
 * 
 */
public class BoundaryMatchers {
    
    public static void main(String[] args) {
        
        String pattern = "([^0-9a-zA-Z])*(\\1)";
        
        //String input = "The dog $$#@# pladf*sdys $ in the yard.";
        String input = "The dog $$#@# pladf*sdys $$#%^$%^^  in the yard.";
        
        Pattern p = Pattern.compile(pattern);
        Matcher m  = p.matcher(input);        
        
        while(m.find()){
            System.out.println("--------------> " + m.group());
        }
                
    }

}
