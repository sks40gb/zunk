package com.datastructure.programs.array;

/**
 *  https://www.geeksforgeeks.org/find-two-missing-numbers-set-1-an-interesting-linear-time-solution/
 * @author sunsingh
 */
public class FindTwoMissingNumber {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 9, 10, 12}; //4 and 8 is missing
        int n = arr.length + 2;
        findMissingNumbers(arr, n);
    }

    public static void findMissingNumbers(int[] arr, int n) {
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
        for (int i = 0; i < n-3; i++) {
            int value = arr[i];
            if (value <= avarage) {
                sumOfBelowAvarage += value;
            } else {
                sumOfAboveAvarage += value;
            }
        }
        
        int expectedSumBelowAvarage = avarage * (avarage+1) / 2;
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
