package com.core.datastructure.sorting;

/**
 *
 * @author Sunil
 */
public class MergeApp {

    public static void main(String[] args) {
        int[] array = {2, 3, 9, 5, 7, 6, 1, 8, 4};
        split(array);
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

    /**
     * Time complexity  - O(n log n)  at worst case
     * Space complexity - O(n)
     * 
     * @param array
     */
    private static void split(int array[]) {

        if (array.length < 2) {
            return;
        }

        int mid = array.length / 2;

        int left[] = new int[mid];
        int right[] = new int[array.length - mid];

        for (int i = 0; i < mid; i++) {
            left[i] = array[i];
        }

        for (int i = 0; i < array.length - mid; i++) {
            right[i] = array[i + mid];
        }

        split(left);
        split(right);
        merge(array, left, right);

    }

    /**
     * Merge two arrays into single Sorted array
     *
     * @param array - Sorted array
     * @param left - Left array
     * @param right - right array
     */
    private static void merge(int[] array, int[] left, int[] right) {
        int leftIndex = 0;
        int rightIndex = 0;
        int arrayIndex = 0;
        while (leftIndex < left.length && rightIndex < right.length) {
            if (left[leftIndex] < right[rightIndex]) {
                array[arrayIndex] = left[leftIndex];
                leftIndex++;
            } else {
                array[arrayIndex] = right[rightIndex];
                rightIndex++;
            }
            arrayIndex++;
        }

        while (leftIndex < left.length) {
            array[arrayIndex] = left[leftIndex];
            leftIndex++;
            arrayIndex++;
        }

        while (rightIndex < right.length) {
            array[arrayIndex] = right[rightIndex];
            rightIndex++;
            arrayIndex++;
        }
    }
}
