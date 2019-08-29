package com.datastructure.programs.sorting;

/**
 *
 * @author Sunil
 */
public class BubbleApp {

    public static void main(String[] args) {
        int[] array = {2, 3, 9, 5, 7, 6, 1, 8, 4};
        bubbleSorting(array);
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

    /**
     * Best case  - O(n)
     * Worse case - O(n²)
     * 
     * @param array
     */
    private static void bubbleSorting(int array[]) {

        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < (array.length - i - 1); j++) {
                if (array[j] > array[j + 1]) {
                    int v = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = v;
                }
            }
        }

    }
}
