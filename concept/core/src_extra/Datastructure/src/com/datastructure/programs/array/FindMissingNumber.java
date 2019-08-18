package com.datastructure.programs.array;

/**
 *
 * @author sunsingh
 */
public class FindMissingNumber {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 6, 7, 8, 9, 10}; //5
        System.out.println(findOneMissingNumber(arr, 10));

        int[] arr2 = {1, 2, 3, 4, 5, 6, 7, 9, 10, 12}; //4 and 8 is missing
        int n = arr.length + 2;
        findTwoMissingNumbers(arr2, n);
    }

    public static int findOneMissingNumber(int[] arr, int n) {
        int missing = n * (n + 1) / 2;
        //System.out.println("missing "+ missing);
        for (int i = 0; i < arr.length; i++) {
            missing = missing - arr[i];
        }
        return missing;
    }

    public static void findTwoMissingNumbers(int[] arr, int n) {
        int[] missing = new int[2];
        //Actual sum
        int expectedSum = n * (n + 1) / 2;
        //Current sum with missing items
        int sum = getSum(arr);
        //Avarage of missing items
        int avarage = (expectedSum - sum) / 2;

        //get sum of numbers lower than avarage;
        int sumOfBelowAvarage = 0;
        int sumOfAboveAvarage = 0;
        for (int i = 0; i < n - 3; i++) {
            int value = arr[i];
            if (value <= avarage) {
                sumOfBelowAvarage += value;
            } else {
                sumOfAboveAvarage += value;
            }
        }

        int expectedSumBelowAvarage = avarage * (avarage + 1) / 2;
        int firstMissingItem = expectedSumBelowAvarage - sumOfBelowAvarage;
        int secondMissingItem = expectedSum - firstMissingItem - sum;

        System.out.println("First missing item " + firstMissingItem);
        System.out.println("Second missing item " + secondMissingItem);
    }

    public static int getSum(int[] items) {
        int sum = 0;
        for (int i = 0; i < items.length; i++) {
            sum += items[i];
        }
        return sum;
    }
}
