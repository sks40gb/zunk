package com.datastructure.programs.array;

/**
 *
 * @author sunsingh
 */
public class FindDuplicate {

    // Driver program  
    public static void main(String[] args) {
        int arr[] = {1, 2, 3, 1, 3, 6, 6};
        int arr_size = arr.length;

        new FindDuplicate().printRepeating(arr);
    }

    // Function to print duplicates 
    void printRepeating(int arr[]) {
        for (int i = 0; i < arr.length; i++) {

            int index = Math.abs(arr[i]);
            if (arr[index] < 0) {
                System.out.println("Duplicate found " + Math.abs(arr[i]));
            } else {
                arr[index] = -arr[index];
            }

        }
    }

}
