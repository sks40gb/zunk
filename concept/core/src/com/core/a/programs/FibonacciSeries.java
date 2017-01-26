package com.core.a.programs;

/**
 * Fibonacci series is series of natural number where next number is equivalent to sum of previous two number e.g. fn =
 * fn-1 + fn-2.
 *
 * @author Sunil
 */
public class FibonacciSeries {

    public static void main(String[] args) {
        fibonacci(10);
    }

    public static void fibonacci(int num) {
        int first = 1;
        int second = 0;
        for (int i = 0; i < num; i++) {
            System.out.println(first + second);
            int temp = second;
            second = first + second;
            first = temp;
        }

    }

}
