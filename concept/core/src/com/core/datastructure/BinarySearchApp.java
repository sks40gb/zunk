package com.core.datastructure;

/**
 *
 * @author Sunil
 */
public class BinarySearchApp {

    public static void main(String[] args) throws Exception {
        int array[] = {1, 3, 6, 99, 102, 120};
        int index = binarySearch(array, 0, array.length - 1, 120);
        System.out.println(index);
    }

    public static int binarySearch(int arr[], int fromIndex, int toIndex, int item) {
        if (toIndex >= fromIndex) {
            int midIndex = fromIndex + (toIndex - fromIndex) / 2;

            // If the element is present at the 
            // middle itself 
            if (arr[midIndex] == item) {
                return midIndex;
            }

            // If element is smaller than mid, then 
            // it can only be present in left subarray 
            if (arr[midIndex] > item) {
                return binarySearch(arr, fromIndex, midIndex - 1, item);
            }

            // Else the element can only be present 
            // in right subarray 
            return binarySearch(arr, midIndex + 1, toIndex, item);
        }

        // We reach here when element is not present 
        // in array 
        return -1;
    }

}
