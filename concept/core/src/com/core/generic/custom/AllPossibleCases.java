package com.core.generic.custom;

import com.core.generic.Physics;
import com.core.generic.Subject;
import java.util.function.Predicate;

/**
 *
 * There is three important cases -
 *
 * 1. Declaring Generic Type - While creating the class which define the custom Generic Type, Generic Type should be
 * declared, it could be world wide ( It can accept any type of class ) or restricted type ( subclass or super class )
 * You can see several option in this class. Declared at class level and method level.
 *
 *
 * 2. Defining the Generic Type - It is restricted that it should have same Type (class) Ex : 
 * List<String> list = new ArrayList<String>();  - correct, same class String declared both side 
 * List<String> list = new ArrayList();          - correct, nothing is declared on right 
 * List<Object> list = new ArrayList<String>();  - incorrect, even String is subclass of
 * Object, it won't compile. Generic Type should be the same.
 *
 * 3. Uses of Generic Type - It follows the Inheritance concept ex: List<Object> list = new ArrayList(); list.add("01");
 * // correct list.add(2); // correct
 *
 * @author Sunil
 */
public class AllPossibleCases<A,B extends Subject> {
//public class AllPossibleCases<A, B super Subject> {   <-- invalid, we cannot use super while defining generics.

    A a;
    B b;

    public A getInstance() {
        return a;
    }

    public B getB() {
        return b;
    }

    public <C extends Subject> C getC() {
        C c = null;
        return c;
    }

    public <D> D getD() {
        D d = null;
        return d;
    }

    public <E> E getE(E d1) {
        E e = null;
        return e;
    }

    public static void main(String[] args) {
        AllPossibleCases<String, Subject> s = new AllPossibleCases<>();

        AllPossibleCases<String, ? super Subject> s2 = new AllPossibleCases<>();
        AllPossibleCases<String, ?> s3 = new AllPossibleCases<>();
        AllPossibleCases<String, ? extends Subject> s4 = new AllPossibleCases<>();

        // B
        Subject subject = s.getB();
        //Physics m = s.getB();

        // C
        subject = s.getC();
        Physics m1 = s.getC();

        Double d1 = s.getD();
        String d2 = s.getD();

        String e1 = s.getE("SUN");
        Integer e2 = s.getE(5);

    }
}
