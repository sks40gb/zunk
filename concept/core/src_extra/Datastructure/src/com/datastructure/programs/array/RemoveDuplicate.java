package com.datastructure.programs.array;

/**
 *
 * @author sunsingh
 */
public class RemoveDuplicate {

    public static void main(String[] args) {
        int arr[] = {10, 20, 20, 30, 30, 40, 50, 50, 60, 60, 60, 60, 60, 70, 70, 70, 70, 70};
        int length = removeDuplicateElements(arr);
        for (int i = 0; i < length; i++) {
            System.out.print(arr[i] + " ");
        }
    }
    public static int removeDuplicateElements(int arr[]) {
        int currentIndex = 0;
        for (int i = 0; i < arr.length-1; i++) {
            if(arr[i] != arr[i+1]){
                arr[currentIndex] = arr[i];
                currentIndex++;
            }
            
        }
        arr[currentIndex] = arr[arr.length -1];
        return currentIndex + 1;
    }

}
