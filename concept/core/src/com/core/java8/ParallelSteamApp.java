package com.core.java8;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sks
 */
public class ParallelSteamApp {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.println("In serial fashion");
        numbers.stream().forEach(number -> System.out.println(ParallelSteamApp.doubleIt(number)));
        System.out.println("In parallel fashion");
        numbers.parallelStream().forEach(number -> System.out.println(ParallelSteamApp.doubleIt(number)));
    }

    public static int doubleIt(int number) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return number * 2;
    }

}
