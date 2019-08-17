package com.datastructure.programs.string;

/**
 *
 * @author sunsingh
 */
public class StringRotation {

    public static void main(String[] args) {
        String s1 = "IndiaUSAEngland";
        String s2 = "USAEnglandIndia";
        System.out.println(isRotation(s1, s2));
    }

    public static boolean isRotation(String s1, String s2) {
        if (s1 == null || s2 == null || s1.length() != s2.length()) {
            return false;
        } else {
            int matchIndex = 0;
            while (matchIndex < s1.length()) {
                int index = nextMatchedIndex(s1.charAt(0), s2, matchIndex + 1);
                if (index == -1) {
                    return false;
                } else {
                    boolean isMatched = isContentMatched(s1, s2, index);
                    if(isMatched){
                        return true;
                    }else{
                        matchIndex = index;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isContentMatched(String s1, String s2, int startIndex) {
        int length = s2.length();
        //startIndex = length % startIndex;
        for (int i = 0; i < s1.length(); i++, startIndex++) {
            if(startIndex >= length){
                startIndex = 0;
            }
            if (s1.charAt(i) != s2.charAt(startIndex)) {
                return false;
            }
        }
        return true;
    }

    public static int nextMatchedIndex(char c, String s2, int startIndex) {
        for (int i = startIndex; i < s2.length(); i++) {
            if (s2.charAt(i) == c) {
                return i;
            }
        }
        return -1;
    }

}
