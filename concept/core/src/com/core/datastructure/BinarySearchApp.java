package com.core.datastructure;

/**
 *
 * @author Sunil
 */
public class BinarySearchApp {

    public static void main(String[] args) throws Exception {
        int array[] = {11, 22, 33, 44, 55, 66, 77, 88};
        int index = binarySearch(array, 707, 0, array.length - 1);
        System.out.println(index);
    }

    public static int binarySearch(int[] array, int key, int fromIndex, int toIndex) {
        if (fromIndex >= toIndex) {
            return -1;
        } else {
            int midIndex = (fromIndex + toIndex) / 2;
            int midValue = array[midIndex];
            if (midValue == key) {
                return midIndex;
            } else if (key > midValue) {
                return binarySearch(array, key, midIndex + 1, toIndex);
            } else if (key < midValue) {
                return binarySearch(array, key, fromIndex, midIndex - 1);
            }
        }
        return -1;
    }

}
