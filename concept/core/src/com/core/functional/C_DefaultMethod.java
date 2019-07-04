package com.core.functional;

/**
 * Default method or Virtual Extension Method or Defender Method.
 *
 * Default method cannot overwrite the Object methods. Ex : hashcode()
 * 
 * If a class is implementing multiple interfaces and those interfaces has the same default method
 * then that method must be overwritten by class, else compiler will get confused.
 * @Refer - exampleMultipleInheritance
 * 
 *
 * @author sunsingh
 */
public class C_DefaultMethod {

    public static void main(String[] args) {
        //exampleOne();
        exampleMultipleInheritance();
    }

    public static void exampleOne() {
        System.out.println("Bycycle Info---------------");
        Vehicle bycycle = new Bycycle();
        bycycle.drive();
        bycycle.fuelType();
        System.out.println("Car Info ---------------");
        Vehicle car = new Bycycle();
        car.drive();
        car.fuelType();

    }
    
     public static void exampleMultipleInheritance() {
        Puffins puffins = new Puffins();
        puffins.swim();
        puffins.fly();
        puffins.speed();

    }

}

/**
 * ======================= Example One ==============================
 */
interface Vehicle {

    void drive();

    default void fuelType() {
        System.out.println("Fuel Type is : Petrol");
    }
}

class Bycycle implements Vehicle {

    @Override
    public void drive() {
        System.out.println("Driving Bicycle");
    }

}

class Car implements Vehicle {

    @Override
    public void drive() {
        System.out.println("Driving Car");
    }

    @Override
    public void fuelType() {
        System.out.println("Fuel Type is : Desiel");
    }

}

/**
 * ======== Multiple inheritance by interface having same method name ===============
 */
interface CanSwim {

    public void swim();

    default public void speed() {
        System.out.println("Speed is 10km/h");
    }
}

interface CanFly {

    public void fly();

    default public void speed() {
        System.out.println("Speed is 60km/h");
    }
}

class Puffins implements CanSwim, CanFly {

    @Override
    public void swim() {
        System.out.println("Puffins can swim");
    }

    @Override
    public void speed() {
        //CanSwim.super.speed();
        CanFly.super.speed();
    }

    @Override
    public void fly() {
        System.out.println("Puffins can fly");
    }

}

