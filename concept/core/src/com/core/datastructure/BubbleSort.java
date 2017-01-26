package com.core.datastructure;

/**
 * Java program to sort integer array using bubble sort sorting algorithm.
 * bubble sort is one of the simplest sorting algorithm but performance
 * of bubble sort is not good, its average and worst case performance
 * ranges in O(n2) and that's why it is not used to sort large set of
 * unsorted data. Bubble sort can be used for educational and testing
 * purpose to sort small number of data to avoid performance penalty.
 * This program is also a good example of how to print contents of Array in Java
 *
 * @author Sunil
 */
public class BubbleSort {

    public static void main(String a[]) {
        int i;
        int array[] = {12, 9, 4};
        
        
        System.out.println("Values Before the sort:\n");
        for (i = 0; i < array.length; i++) {
            System.out.print(array[i] + "  ");
        }
        System.out.println();
        sort(array);
        System.out.print("Values after the sort:\n");
        for (i = 0; i < array.length; i++) {
            System.out.print(array[i] + "  ");
        }
        System.out.println();
        System.out.println("PAUSE");
    }

    public static void sort(int array[]) {
        int size = array.length;
        for (int i = 0; i < size; i++) {            
            for (int j = 1; j < (size - i); j++) {
                if (array[j - 1] > array[j]) {
                    int t = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = t;
                }
            }
        }
    }
}