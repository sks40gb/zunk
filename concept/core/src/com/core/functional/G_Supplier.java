package com.core.functional;

import java.util.function.Supplier;

/**
 *
 * @author sunsingh
 */
public class G_Supplier {

    public static void main(String[] args) {
        exampleOne();
    }

    public static void exampleOne() {
        Supplier<Integer> s = () -> (int) (Math.random() * 10);

        System.out.println("RANDOM NUMBER : " + s.get());

    }

}
