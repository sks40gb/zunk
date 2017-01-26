/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.csit.util;

/**
 *
 * @author sunil
 */
public class Common {

    public static String getFommattedName(String originalText) {
        StringBuffer buffer = new StringBuffer();        
        char characters[] = originalText.toCharArray();
        boolean isFirst = true;
        for (char c : characters) {
            if (isFirst) {
                buffer.append(Character.toUpperCase(c));
                isFirst = false;
            } else {
                if (Character.isUpperCase(c)) {
                    buffer.append(" ");
                }if(c == '_'){
                    c = ' ';
                }
                buffer.append(c);
            }
        }
        return buffer.toString();
    }


    public static void main(String[] args) {

        StringBuffer buffer = new StringBuffer();
        String originalText = "sunilKumar_Singh";
        char characters[] = originalText.toCharArray();
        boolean isFirst = true;
        for (char c : characters) {
            if (isFirst) {
                buffer.append(Character.toUpperCase(c));
                isFirst = false;
            } else {
                if (Character.isUpperCase(c)) {
                    buffer.append(" ");
                }if(c == '_'){
                    c = ' ';
                }
                buffer.append(c);
            }
        }       
    }
}
