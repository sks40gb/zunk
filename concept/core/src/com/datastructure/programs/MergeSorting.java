package com.datastructure.programs;

import java.util.Arrays;

/**
 *
 * @author sunsingh
 */
public class MergeSorting {

    public static void main(String[] args) {
        int[] arr = {7, 2, 4, 1, 5, 3};
        mergeSort(arr, 0, arr.length - 1);
        Arrays.stream(arr).forEach(System.out::println);
    }

    public static void mergeSort(int[] arr, int low, int high) {
        if (low < high) {
            int middle = (low + high) / 2;
            mergeSort(arr, low, middle);
            mergeSort(arr, middle + 1, high);
            merge(arr, low, middle, high);
        }
    }

    public static void merge(int[] arr, int low, int middle, int high) {
        int[] left = new int[middle - low + 1];
        int[] right = new int[high - middle];

        int[] result = new int[high - low + 1];
        
        //Copy to left temp array
        for (int i = 0; i < left.length; i++) {
            left[i] = arr[i + low];
        }

        //Copy to right temp array
        for (int i = 0; i < right.length; i++) {
            right[i] = arr[i + middle+1];
        }

        //Copy the left and right minimum element first.
        int leftIndex = 0;
        int rightIndex = 0;
        int resultIndex = 0;
        while (leftIndex < left.length && rightIndex < right.length) {
            if (left[leftIndex] < right[rightIndex]) {
                result[resultIndex] = left[leftIndex];
                leftIndex++;
            } else {
                result[resultIndex] = right[rightIndex];
                rightIndex++;
            }
            resultIndex++;
        }
        
        //copy Left leftover item
        while(leftIndex < left.length){
            result[resultIndex] = left[leftIndex];
            leftIndex++;
            resultIndex++;
        }
        
        //copy right left over items
        while(rightIndex < right.length){
            result[resultIndex] =right[rightIndex];
            leftIndex++;
            rightIndex++;
        }
        
        //Copy to existing array
        for (int i = 0; i < result.length; i++) {
            arr[i+low] = result[i];
        }

    }

}
