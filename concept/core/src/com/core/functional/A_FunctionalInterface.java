package com.core.functional;

/**
 * Any interface which has only one absract method is considered and Functional Interface regardless of whether
 *
 * @FunctionalInterface is applied or not.
 *
 * Functional interface must have one and only one abstract method.
 *
 * @author sunsingh
 */
@java.lang.FunctionalInterface
interface AddInter {

    public int add(int a, int b);

    //Compilation error will be thrown if more than one abstract is found.
    //public int testMethod();
}

/**
 *
 * @author sunsingh
 */
public class A_FunctionalInterface {

    public static void main(String[] args) {
        
        AddInter f = (a,b)->a+b;
        System.out.println("TOTAL : " + f.add(10, 20));

    }

}
