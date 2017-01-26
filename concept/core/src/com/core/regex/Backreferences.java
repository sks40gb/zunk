/*
 * Looking Inside The Regex Engine
 * Backreferences can not only be used after a match has been found, 
 * but also during the match. Suppose you want to match a pair of opening and 
 * closing HTML tags, and the text in between. By putting the opening tag into 
 * a backreference, we can reuse the name of the tag for the closing tag.
 */

package com.core.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author sunil
 */
public class Backreferences {
    
    public static void main(String[] args) {
        
        String txt = "SUNILxxSUNILKUMAR";
        String patt = "([A-Z][A-Z0-9]*)xx\\1";
        Pattern p = Pattern.compile(patt);
        Matcher m = p.matcher(txt);
        
        if(m.find())
        {
            System.out.println("---------------------> " + m.group());
        }
    }

}
