package com.datastructure.programs.sorting;

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
        int[] result = new int[high - low + 1];
        
        //Copy the left and right minimum element first.
        int leftIndex = low;
        int rightIndex = middle+1;
        int resultIndex = 0;
        while (leftIndex <= middle && rightIndex <= high) {
            if (arr[leftIndex] < arr[rightIndex]) {
                result[resultIndex] = arr[leftIndex];
                leftIndex++;
            } else {
                result[resultIndex] = arr[rightIndex];
                rightIndex++;
            }
            resultIndex++;
        }
        
        //copy Left leftover item
        while(leftIndex  <= middle){
            result[resultIndex] = arr[leftIndex];
            leftIndex++;
            resultIndex++;
        }
        
        //copy right left over items
        while(rightIndex  <= high){
            result[resultIndex] =arr[rightIndex];
            leftIndex++;
            rightIndex++;
        }
        
        //Copy to existing array
        for (int i = 0; i < result.length; i++) {
            arr[i+low] = result[i];
        }

    }

}
