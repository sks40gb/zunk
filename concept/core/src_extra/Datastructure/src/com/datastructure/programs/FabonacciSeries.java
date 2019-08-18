package com.datastructure.programs;

/**
 *
 * @author sunil
 */
public class FabonacciSeries {

    public static void main(String[] args) {
        fabonacciLoop(10);
    }

    public static void fabonacciLoop(int n) {

        int a = 0;
        int b = 1;
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum = a + b;
            a = b;
            b = sum;
            System.out.println(a);
        }

    }

}
