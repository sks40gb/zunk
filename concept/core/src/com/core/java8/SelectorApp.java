package com.core.java8;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 *
 * @author sks
 */
public class SelectorApp {

    public static void main(String[] args) {

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 6, 7, 8, 9);
        //old fashion
        System.out.println("Old Fashion summing : " + sum(numbers, new EvenSelector()));
        
        //with partial lambda
        System.out.println("Lambda Fashion summing : " + sum(numbers, number-> number %2 == 0));
        
        //with full lambda
        System.out.println("Lambda Fashion summing : " + sumWithLambda(numbers, number-> number %2 == 0));
        
    }
    
    public static int sum(List<Integer> numbers, Selector selector){
        int result = 0;
        for(int number : numbers){
            if(selector.pick(number)){
                result +=  number;
            }
        }
        return result;
    }
    
     public static int sumWithLambda(List<Integer> numbers, Predicate<Integer> predicate){
        int result = 0;
        for(int number : numbers){
            if(predicate.test(number)){
                result +=  number;
            }
        }
        return result;
    }
    
    

}

interface Selector {

    public boolean pick(int number);
}

class EvenSelector implements Selector {

    public boolean pick(int number) {
        return number % 2 == 0;

    }
}
