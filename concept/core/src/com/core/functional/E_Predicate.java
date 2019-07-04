package com.core.functional;

import java.util.function.Predicate;

/**
 * Predicate are used only for conditional check
 *
 * @author sunsingh
 */
public class E_Predicate {

    public static void main(String[] args) {
        //exampleOne();
        exampleNested();
    }

    public static void exampleOne() {
        Predicate<Integer> p = (i) -> i % 2 == 0;
        System.out.println("Is 5 even : " + p.test(5));
        System.out.println("Is 8 even : " + p.test(8));
    }

    public static void exampleNested() {
        Predicate<Integer> isEven = (i) -> i % 2 == 0;
        Predicate<Integer> greaterThan10 = (number)-> number > 10;
        System.out.println("Is 16 greaten 10 and even : " + isEven.and(greaterThan10).test(16));
    }
}
