
package com.datastructure.programs.array;

import java.util.HashMap;
import java.util.Map;

/**

Imagine you have a special keyboard with all keys in a single row. The layout of characters on a keyboard is denoted by a string S1 of length 26. S1 is indexed from 0 to 25. Initially, your finger is at index 0. To type a character, you have to move your finger to the index of the desired character. The time taken to move your finger from index i to index j is |j-i|, where || denotes absolute value.

Write a function solution(), that given a string S1 that describes the keyboard layout and a string S2, returns an integer denoting the time taken to type string S2.

Examples:

S1 = abcdefghijklmnopqrstuvwxyz

S2 = cba

Input : S1 = abcdefghijklmnopqrstuvwxyz, S2 = cba 
Output : 4

https://www.geeksforgeeks.org/google-software-engineering-intern-fall-2019-north-america/ 
*/
public class KeyboardLayout {
    
    public static void main(String[] args) {
        String s1 = "abcdefghijklmnopqrstuvwxyz";
        String s2 = "cba";
        System.out.println("Total moves " +  solution(s1, s2));
    }
    
    public static int solution(String s1, String s2){
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < s1.length(); i++) {
            map.put(s1.charAt(i), i);
        }
        int moveSteps = 0;
        int currentIndex = 0;
        for (int i = 0; i < s2.length(); i++) {
          char c = s2.charAt(i);
          int diff = Math.abs(currentIndex - map.get(c));
          currentIndex = i;
          moveSteps += diff;
        }
        
        return moveSteps;
    }
    
}
