package com.core.datastructure.sorting;

/**
 *
 * @author Sunil
 */
public class QuickApp {

    public static void main(String[] args) {
        int[] array = {2, 3, 9, 5, 7, 6, 1, 8, 4};
        quick(array, 0, array.length - 1);
        for (int i = 0; i < array.length - 1; i++) {
            System.out.println(array[i]);
        }
    }

    /**
     * Time complexity - O(n log n) at average case. 
     * Space complexity - O(n²) at worst case.
     */
    private static void quick(int array[], int start, int end) {
        if (start < end) {
            int pivotIndex = partition(array, start, end);
            quick(array, start, pivotIndex - 1);
            quick(array, pivotIndex + 1, end);
        }
    }

    private static int partition(int[] array, int start, int end) {
        int partinationIndex = start;
        int pivot = array[end];
        for (int i = start; i < end; i++) {
            if (array[i] < pivot) {
                int temp = array[partinationIndex];
                array[partinationIndex] = array[i];
                array[i] = temp;
                partinationIndex++;
            }
        }
        int temp = array[partinationIndex];
        array[partinationIndex] = array[end];
        array[end] = temp;
        return partinationIndex;
    }
}
