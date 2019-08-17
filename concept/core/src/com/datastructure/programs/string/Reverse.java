package com.datastructure.programs.string;

/**
 *
 * @author sunsingh
 */
public class Reverse {

    public static void main(String[] args) {
        String str = "abcde";
        System.out.println(reverse(str));
    }

    public static String reverse(String str) {
        if (str.length() == 0) {
            return "";
        }
        return reverse(str.substring(1)) + str.charAt(0);
    }
}
