package com.core.datastructure;

/**
 *
 * @author Sunil
 */
public class BinarySearchApp {

    public static void main(String[] args) throws Exception {
        int array[] = {1, 3, 6, 99, 102, 120};
        int index = recursiveBinarySearch(array, 0, array.length - 1, 120);
        System.out.println(index);
    }

    //Method 1
    public static int recursiveBinarySearch(int arr[], int fromIndex, int toIndex, int item) {
        if (fromIndex <= toIndex) {
            int midIndex = fromIndex + (toIndex - fromIndex) / 2;
            if (arr[midIndex] == item) {
                return midIndex;
            }
            if (arr[midIndex] > item) {
                return recursiveBinarySearch(arr, fromIndex, midIndex - 1, item);
            }
            return recursiveBinarySearch(arr, midIndex + 1, toIndex, item);
        }
        return -1;
    }

    //Method 2
    public static int iteratrorBinarySearch(int[] array, int key, int fromIndex, int toIndex) {
        while (fromIndex <= toIndex) {
            int midIndex = fromIndex + (toIndex - fromIndex) / 2;
            if (key == array[midIndex]) {
                return midIndex;
            } else if (key > array[midIndex]) {
                fromIndex = midIndex + 1;
            } else {
                toIndex = midIndex - 1;
            }

        }
        return -1;
    }

}
