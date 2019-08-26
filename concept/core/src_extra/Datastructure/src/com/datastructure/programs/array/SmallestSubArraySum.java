package com.datastructure.programs.array;

/**
 *
 * @author sunil
 */
public class SmallestSubArraySum {

    public static void main(String[] args) {
        int[] arr = {3, -4, 2, -3, -1, 7, -5}; //[m x n]
        System.out.println("Min " + minSum(arr));

        int[] index = minSumBracket(arr);
        System.out.println("startIndex " + index[0] + " endIndex " + index[1]);
    }

    public static int minSum(int[] arr) {
        int min = Integer.MAX_VALUE;
        int minSoFar = Integer.MAX_VALUE;

        for (int i = 0; i < arr.length; i++) {
            if (min > 0) {
                min = arr[i];
            } else {
                min += arr[i];
                minSoFar = Math.min(min, minSoFar);
            }
        }
        return minSoFar;

    }

    public static int[] minSumBracket(int[] arr) {
        int min = Integer.MAX_VALUE;
        int minSoFar = Integer.MAX_VALUE;
        int startIndex = 0;
        int endIndex = 0;
        for (int i = 0; i < arr.length; i++) {
            if (min > 0) {
                min = arr[i];
                if (min < minSoFar) {
                    startIndex = i;
                }
            } else {
                min += arr[i];
                if (min < minSoFar) {
                    endIndex = i;
                }
            }
            minSoFar = Math.min(min, minSoFar);
        }

        return new int[]{startIndex, endIndex};
    }

}
