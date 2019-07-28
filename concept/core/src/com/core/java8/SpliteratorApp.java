package com.core.java8;

import java.util.ArrayList;
import java.util.Spliterator;
import java.util.stream.Stream;

public class SpliteratorApp {

    public static void main(String[] args) {
        // Create an array list for doubles. 
        ArrayList<Integer> list = new ArrayList<>();

        // Add values to the array list. 
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);

        example1(list);
        example2(list);
    }

    public static void example1(ArrayList<Integer> list) {
        // Obtain a Stream to the array list. 
        Stream<Integer> str = list.stream();

        // getting Spliterator object on al 
        Spliterator<Integer> splitr1 = str.spliterator();

        // estimateSize method 
        System.out.println("estimate size : " + splitr1.estimateSize());

        // getExactSizeIfKnown method 
        System.out.println("exact size : " + splitr1.getExactSizeIfKnown());

        // hasCharacteristics and characteristics method 
        System.out.println(splitr1.hasCharacteristics(splitr1.characteristics()));

        System.out.println("Content of arraylist :");
        // forEachRemaining method     
        splitr1.forEachRemaining((n) -> System.out.println(n));
    }

    public static void example2(ArrayList<Integer> list) {
        // Obtain a Stream to the array list. 
        Stream<Integer> str = list.stream();

        // getting Spliterator object on al 
        Spliterator<Integer> splitr1 = str.spliterator();
        // Obtaining another  Stream to the array list. 
        Stream<Integer> str1 = list.stream();
        splitr1 = str1.spliterator();

        // trySplit() method 
        Spliterator<Integer> splitr2 = splitr1.trySplit();

        // If splitr1 could be split, use splitr2 first. 
        if (splitr2 != null) {
            System.out.println("Output from splitr2: ");
            splitr2.forEachRemaining((n) -> System.out.println(n));
        }

        // Now, use the splitr 
        System.out.println("\nOutput from splitr1: ");
        splitr1.forEachRemaining((n) -> System.out.println(n));
    }
}
