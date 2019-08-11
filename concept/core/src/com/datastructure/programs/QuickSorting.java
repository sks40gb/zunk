package com.datastructure.programs;

import java.util.Arrays;

/**
 *
 * @author sunsingh
 */
public class QuickSorting {

    public static void main(String[] args) {
        int[] arr = {7, 2, 4, 1, 5, 3};
        quickSort(arr, 0, arr.length - 1);
        Arrays.stream(arr).forEach(System.out::println);
    }

    public static void quickSort(int[] arr, int left, int right) {
        if (left <= right) {
            int pIndex = getPartition(arr, left, right);
            quickSort(arr, left, pIndex-1);
            quickSort(arr, pIndex+1, right);
        }
    }

    public static int getPartition(int[] arr, int left, int right) {
        int pivot = arr[right];
        int pIndex = left;
        for (int i = left; i < right ; i++) {
            if (arr[i] < pivot) {
                swap(arr, i, pIndex);
                pIndex++;
            }
        }
        swap(arr, pIndex, right);
        return pIndex;
    }

    public static void swap(int[] arr, int i, int j) {
        int tempValue = arr[i];
        arr[i] = arr[j];
        arr[j] = tempValue;
    }

}
