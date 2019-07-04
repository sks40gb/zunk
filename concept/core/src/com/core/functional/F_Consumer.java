package com.core.functional;

import java.util.function.Consumer;

/**
 *
 * @author sunsingh
 */
public class F_Consumer {

    public static void main(String[] args) {
        exampleOne();
    }

    public static void exampleOne() {

        Consumer<Integer> c = n -> System.out.println("Printing Number : " + n);;
        c.accept(100);

    }

}
