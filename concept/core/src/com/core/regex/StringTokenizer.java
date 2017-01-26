/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.regex;

/**
 *
 * @author sunil
 */
public class StringTokenizer {
    public static void main(String[] args) {
        String str = "sunil|kumar|singh";
        String deli = "\\|";
        
        String tokens[]  = str.split(deli);
        
        for(String token : tokens)
        {
            System.out.println("==========> " + token);
        }
        
    }   
    
}
