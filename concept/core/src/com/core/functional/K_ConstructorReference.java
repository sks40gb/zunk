package com.core.functional;

import java.util.function.Consumer;

/**
 * Matched constructor will be called in case of multiple constructors.
 * @author sunsingh
 */
public class K_ConstructorReference {

    public static void main(String[] args) {
        Consumer<Integer> c = ReferenceEmployee::new;
        c.accept(100);
    }
}

class ReferenceEmployee {

    public ReferenceEmployee(int id) {
        System.out.println("Employee id " + id);
    }
}
