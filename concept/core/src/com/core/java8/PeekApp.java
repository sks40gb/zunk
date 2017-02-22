package com.core.java8;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Sunil
 */
public class PeekApp {
    
    public static void main(String[] args) {
         List<Integer> list = Arrays.asList(1, 10, 3, 7, 5);
        int a = list.stream()
                //Peek method is used to debug the functional programming where you can check the value in between.
                .peek(num -> System.out.println("will filter " + num))
                .filter(x -> x > 5)
                .findFirst()
                .get();

        System.out.println("found : " + a);
    }

}
