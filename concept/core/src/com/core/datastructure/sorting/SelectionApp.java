package com.core.datastructure.sorting;

/**
 *
 * @author Sunil
 */
public class SelectionApp {

    public static void main(String[] args) {
        int[] array = {2, 3, 9, 5, 7, 6, 1, 8, 4};
        selectionSorting(array);
        for (int i = 0; i < array.length; i++) {
            System.out.println(array[i]);
        }
    }

    /**
     * @param array 
     */
    private static void selectionSorting(int array[]) {

        for (int i = 0; i < array.length; i++) {
            int minIndex = i;
            for (int j = i; j < array.length; j++) {
                if(array[j] < array[minIndex]){
                    minIndex = j;
                }
            }
            int temp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = temp;            
        }
    }

}
