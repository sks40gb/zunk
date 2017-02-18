package com.core.java8;

/**
 *
 * @author sks
 */
public class InterfaceApp {

    public static void main(String[] args) {
        Car car = new Car();
        car.print();
        car.getInfo();
    }

}

interface Vehicle {

    public void print();

    default public void getInfo() {
        System.out.println("Printing information from Vehicle " + this);
    }
}

class Car implements Vehicle {

    public void print() {
        System.out.println("Printing car information ");
    }
}
