package com.datastructure.programs;

/**
 *
 * @author sunsingh
 */
public class PowerOfTwo {

    public static void main(String[] args) {
        System.out.println(isPowerOfTwo(8));
    }

    public static boolean  isPowerOfTwo(int n) {
        return (n & (n - 1)) == 0;
    }
}
