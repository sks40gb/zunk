package com.datastructure.programs.array;

/**
 * Find the element that appears once in an array where every other element appears twice
 * https://www.geeksforgeeks.org/find-element-appears-array-every-element-appears-twice/
 * @author sunsingh
 */
public class FindSingle {

    public static void main(String[] args) {
        int[] arr = {1,1,2,2,3,3,4,5,5};
        System.out.println("Item is " + getNonDuplicateNumber(arr)); // Answer is 4
    }
    
    public static int getNonDuplicateNumber(int[] arr){
        int item = 0;
        for (int i = 0; i < arr.length; i++) {
            item = item ^ arr[i];
        }
        return item;
    }
}
