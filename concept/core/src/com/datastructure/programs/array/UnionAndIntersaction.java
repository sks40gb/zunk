package com.datastructure.programs.array;

/**
 * Write a program to find intersection of two sorted arrays in Java Read more:
 * https://javarevisited.blogspot.com/2015/06/top-20-array-interview-questions-and-answers.html#ixzz5wPgp7wiz
 *
 * @author sunsingh
 */
public class UnionAndIntersaction {

    public static void main(String[] args) {
        int[] arr1 = {1, 3, 4, 5, 7};
        int[] arr2 = {2, 3, 5, 6};
        //print(union(arr1, arr2));
        print(intersaction(arr1, arr2));

    }

    public static int[] union(int[] arr1, int[] arr2) {
        int i = 0;
        int j = 0;
        int pIndex = 0;
        int[] union = new int[arr1.length + arr2.length];
        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] == arr2[j]) {
                union[pIndex++] = arr1[i];
                i++;
                j++;
            } else if (arr1[i] < arr2[j]) {
                union[pIndex++] = arr1[i];
                i++;
            } else if (arr1[i] > arr2[j]) {
                union[pIndex++] = arr2[j];
                j++;
            }
        }
        while (i < arr1.length) {
            union[pIndex++] = arr1[i++];
        }
        while (j < arr2.length) {
            union[pIndex++] = arr1[j++];
        }

        return union;
    }

    public static int[] intersaction(int[] arr1, int[] arr2) {
        int i = 0;
        int j = 0;
        int pIndex = 0;
        int[] intesaction = new int[arr1.length];
        while (i < arr1.length && j < arr2.length) {
            if (arr1[i] == arr2[j]) {
                intesaction[pIndex++] = arr1[i];
                i++;
                j++;
            } else if (arr1[i] < arr2[j]) {
                i++;
            } else if (arr1[i] > arr2[j]) {
                j++;
            }
        }
        return intesaction;
    }

    public static void print(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);

        }
    }

}
