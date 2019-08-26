package com.datastructure.programs.array;

/**
 *
 * @author sunsingh
 */
public class CountDuplicate {

    public static void main(String[] args) {
        int arr[] = {1, 2, 2, 2, 3, 3, 5, 5, 5, 5};
        int count = countDuplicate(arr);
        System.out.println("Count : " + count);
    }

    public static int countDuplicate(int arr[]) {
        int count = 0;
        boolean ignore = false;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] == arr[i + 1]) {
                if (!ignore) {
                    count++;
                    ignore = true;
                }
            } else {
                ignore = false;
            }
        }
        return count;
    }

}
