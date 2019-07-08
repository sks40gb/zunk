package com.core.functional;

/**
 * In case of overloading, matched method signature will be used.
 * @author sunsingh
 */
public class K_MethodReference {
    public int random = 345;
    private static void add(int a, int b) {
        System.out.println("Addition of " + a + " and " + b + " is " + a + b);
    }

    private void multi(int a, int b) {
        System.out.println("Multiply of " + a + " and " + b + " is " + a * b + " Random " + this.random);
    }

    public static void main(String[] args) {
        AddFunction add = K_MethodReference::add;
        add.add(10, 20);

        K_MethodReference refObject =  new K_MethodReference();
        AddFunction multiply = refObject::multi;
        refObject.random = 1000;
        
        multiply.add(10, 20);
    }
}

interface AddFunction {

    public void add(int a, int b);
}
