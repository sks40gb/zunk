package com.datastructure.programs.string;

/**
 *
 * @author sunsingh
 */
public class Palindrome {

    public static void main(String[] args) {
        String str = "abba";
        System.out.println(isPolindrome(str));
    }

    public static boolean isPolindrome(String str) {
        int startIndex = 0;
        int endIndex = str.length() - 1;
        while (startIndex <= endIndex) {
            if (str.charAt(startIndex) != str.charAt(endIndex)) {
                return false;
            } else {
                startIndex++;
                endIndex--;
            }
        }
        return true;
    }
}
