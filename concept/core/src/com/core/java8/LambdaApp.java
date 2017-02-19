package com.core.java8;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author sks
 */
public class LambdaApp {

    public static void main(String[] args) {
        List<Integer> numbers = Arrays.asList(1, 7, 2, 9, 3, 4, 10, 5, 6);
        long time = System.currentTimeMillis();
         numbers.stream()
                .filter(e -> e % 2 == 0)
                .filter(e -> e < 50)
                .map(e -> e * 1)
                .sorted((a,b)-> a.compareTo(b))
                .forEach(e -> System.out.println("Element : " + e));
        
       
        System.out.println("finished in " + (System.currentTimeMillis() - time));

    }
    
    public static int sum(int original, int number){
        return original + number;
    }

}
