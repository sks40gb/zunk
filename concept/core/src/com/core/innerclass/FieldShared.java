package com.core.innerclass;

/**
 *
 * @author Sunil
 */
public class FieldShared {

    public static void main(String[] args) {
        MyOuter myOuter = new MyOuter();
        //each time new instance of inner is created. While there is no change in parent class.
        myOuter.makeInner();
        myOuter.makeInner();

        myOuter.method();
    }
}

class MyOuter {

    private int age;

    public void makeInner() {
        new MyInner().print();
    }

    public void method() {
        class MethodClass {

            public void print() {
                System.out.println("INSIDE METHOD INNER CLASS : Age : " + age);
            }
        }
        MethodClass m = new MethodClass();
        m.print();
    }

    class MyInner {

        public void print() {
            System.out.println("Age : " + age);
            System.out.println("Parent this : " + MyOuter.this);
            System.out.println("My this : " + this);
        }
    }
}
