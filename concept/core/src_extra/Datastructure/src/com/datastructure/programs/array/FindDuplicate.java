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
        System.out.println("The repeating elements are : ");
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i] + " : " + Math.abs(arr[i]));
            if (arr[Math.abs(arr[i])] >= 0) {
                arr[Math.abs(arr[i])] = -arr[Math.abs(arr[i])];
            } else {
                System.out.print(Math.abs(arr[i]) + " ");
            }
        }
    }

}
