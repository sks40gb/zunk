package com.datastructure.programs.array;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author sunsingh
 */
public class SumOfPairEqualsTo {

    public static void main(String[] args) {
        int[] a = {1, 2, 4, 7, 3, 9};
        int[] result = sumWithSet(a, 13);
        System.out.println("Total 13 is sum of  " + result[0] + " + " + result[1]);
        
        int[] result2 = sumWithSort(a, 7);
        System.out.println("Total 7 is sum of  " + result2[0] + " + " + result2[1]);
    }

    public static int[] sumWithSet(int[] arr, int sum) {    //O(n)
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < arr.length; i++) {
            if (set.contains(sum - arr[i])) {
                return new int[]{arr[i], sum - arr[i]};
            } else {
                set.add(arr[i]);
            }
        }
        return null;
    }

    public static int[] sumWithSort(int[] arr, int sum) { //O(n logn)
        Arrays.sort(arr);
        int startIndex = 0;
        int endIndex = arr.length-1;
        while (startIndex < endIndex) {
            int s = arr[startIndex] + arr[endIndex];
            if (s > sum) {
                endIndex--;
            } else if (s < sum) {
                startIndex++;
            } else {
                return new int[]{arr[startIndex], arr[endIndex]};
            }
        }
        return null;
    }
}
