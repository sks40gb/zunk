package com.core.java8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 *
 * @author sks
 */
public class PredicateApp {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 6, 7, 8, 9);
        PredicateApp app = new PredicateApp();
//        app.isGreaterThan3(numbers);
        app.isGreaterThan(numbers);

    }
    
    
    
    public void isGreaterThan3(List<Integer> numbers) {
        Predicate<Integer> isGreaterThan3 = number -> number > 3;
        numbers.stream().filter(isGreaterThan3).forEach(e -> System.out.println(e));
    }
    
   public void isGreaterThan(List<Integer> numbers) {
        Function<Integer, Predicate<Integer>> isGreaterThan = pivot -> number -> number > pivot;
        Predicate<Integer> isGreaterThan3 = isGreaterThan.apply(3);
        numbers.stream().filter(isGreaterThan3).forEach(e -> System.out.println(e));
    }
        


}
