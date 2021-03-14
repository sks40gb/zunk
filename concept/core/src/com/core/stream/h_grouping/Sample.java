package com.core.stream.h_grouping;

import java.util.*;
import static java.util.stream.Collectors.*;

public class Sample {  
  public static List<Person> createPeople() {
    return List.of(
	    new Person("Sara", 20),
	    new Person("Sara", 22),
	    new Person("Bob", 20),
	    new Person("Paula", 32),
	    new Person("Paul", 32),
	    new Person("Jack", 2),
	    new Person("Jack", 72),
	    new Person("Jill", 12)
	  );
  }

  public static void main(String[] args) {
    List<Person> people = createPeople();
    
    Map<String, List<Person>> byName =
      people.stream()
        .collect(groupingBy(person -> person.getName()));
        
    System.out.println(byName);
  }
}
