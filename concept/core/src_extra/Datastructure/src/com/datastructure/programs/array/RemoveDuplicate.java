package com.datastructure.programs.array;

/**
 *
 * @author sunsingh
 */
public class RemoveDuplicate {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 4, 4, 5,5,5,5};
        new RemoveDuplicate().removeDuplicate(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    public void removeDuplicate(int[] arr) {
        int pIndex = 1;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] != arr[i - 1]) {
                arr[pIndex] = arr[i];
                pIndex++;
            }
        }
    }
}
