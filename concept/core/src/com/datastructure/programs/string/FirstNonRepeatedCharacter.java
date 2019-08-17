package com.datastructure.programs.string;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sunil
 */
public class FirstNonRepeatedCharacter {
    public static void main(String[] args) {
        String s = "abcabd";
        System.out.println(nonRepeativeChar(s));
    }
    
    public static Character nonRepeativeChar(String str){
        char[] chars = str.toCharArray();
        Map<Character, Integer> map = new HashMap<>();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            int count = 0;
            if(map.containsKey(aChar)){
                count = map.get(aChar);
            }
            count++;
            map.put(aChar, count);
        }
        
        for (Map.Entry<Character, Integer> entry : map.entrySet()) {
            if(entry.getValue() == 1){
                return entry.getKey();
            }
        }
        return null;
    }
}
