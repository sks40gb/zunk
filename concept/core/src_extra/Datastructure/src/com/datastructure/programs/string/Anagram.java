package com.datastructure.programs.string;

import java.util.Arrays;

/**
 *
 * @author sunil
 */
public class Anagram {
    
    public static void main(String[] args) {
        String s1 =  "wait";
        String s2 = "twai";
        String s3 = "wiak";
        
        System.out.println(isAnagram(s1, s2));
        System.out.println(isAnagram(s1, s3));
        
    }
    
    public static boolean isAnagram(String s1,String s2){
        if(s1 == null || s2 == null || (s1.length() != s2.length())){
            return false;
        }else{
            char[] arr1 = s1.toCharArray();
            char[] arr2 = s2.toCharArray();
            
            Arrays.sort(arr1);
            Arrays.sort(arr2);
            for(int i=0; i < arr1.length; i++){
                if(arr1[i] != arr2[i]){
                    return false;
                }
            }
        }
        return true;
    }
    
}
