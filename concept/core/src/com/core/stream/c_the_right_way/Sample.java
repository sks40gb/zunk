package com.core.stream.c_the_right_way;

import java.util.*;
import static java.util.stream.Collectors.*;

public class Sample {  
  public static void main(String[] args) {
    List<Integer> numbers = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    //Create a list of double of even numbers
    
    //The right way, delegate, be declarative, leave it to the APIs
    
    List<Integer> doubleOfEven = numbers.stream()
      .filter(e -> e % 2 == 0)
      .map(e -> e * 2)
      .collect(toList());
      
    System.out.println(doubleOfEven);
  }
}
