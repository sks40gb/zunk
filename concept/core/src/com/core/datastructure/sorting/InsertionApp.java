package com.core.datastructure.sorting;

/**
 *
 * @author Sunil
 */
public class InsertionApp {

    public static void main(String[] args) {
        int[] array = {2, 3, 9, 5, 7, 6, 1, 8, 4};
        insertionSorting(array);
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
    private static void insertionSorting(int array[]) {

        for (int i = 1; i < array.length; i++) {
            int value = array[i];
            int hole = i;
            while (hole > 0 && value < array[hole - 1]) {
                array[hole] = array[hole - 1];
                hole--;
            }
            array[hole] = value;
        }

    }

}
