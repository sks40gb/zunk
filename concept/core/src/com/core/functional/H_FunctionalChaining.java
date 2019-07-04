package com.core.functional;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author sunsingh
 */
public class H_FunctionalChaining {

    public static void main(String[] args) {
        nestedConsumerExample();
        //nestedFunctionExample();
    }

    private static void nestedConsumerExample() {
        Consumer<String> print = (s) -> System.out.println("Printing the intial value " + s);
        Consumer<String> uppercase = s -> System.out.println("Uppercase : " + s.toUpperCase());
        print.andThen(uppercase).accept("Sunil.Singh");

    }

    private static void nestedFunctionExample() {
        Function<String, String> split = (s) -> s.split("\\.")[0];
        Function<String, String> uppercase = (s) -> s.toUpperCase();
        String result = split.andThen(uppercase).apply("Sunil.Singh");
        System.out.println("Sunil.Singh is converted into : " + result);

    }

}
