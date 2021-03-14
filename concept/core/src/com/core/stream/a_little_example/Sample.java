package com.core.stream.a_little_example;

import java.util.*;

public class Sample {  
  public static void main(String[] args) {
    List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    //Print the double of event numbers
    
    numbers.stream()
      .filter(e -> e % 2 == 0)
      .map(e -> e * 2)
      .forEach(System.out::println);
  }
}
