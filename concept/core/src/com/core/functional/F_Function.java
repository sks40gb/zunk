package com.core.functional;

import java.util.function.Function;

/**
 *
 * @author sunsingh
 */
public class F_Function {

    public static void main(String[] args) {
        exampleOne();
    }

    public static void exampleOne() {

        Function<Integer, Integer> sqr = n -> n * n;
        Integer result = sqr.apply(10);
        System.out.println("Sqrt of 10 is " + result);

    }

}
