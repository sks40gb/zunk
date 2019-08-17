package com.datastructure.programs.array;

/**
 *
 * @author sunsingh
 */
public class FindMissingNumber {

    public static void main(String[] args) {
        int[] arr = {1,2,3,4,6,7,8,9,10}; //5
        System.out.println(findMissing(arr, 10));
    }
    
    public static int findMissing(int[] arr, int n){
        int missing = n*(n+1)/2;
        //System.out.println("missing "+ missing);
        for (int i = 0; i < arr.length; i++) {
            missing = missing -  arr[i];
        }
        return missing;
    }
}
