package com.sun.designpattern.structural.bridge;

/**
 *
 * Bridge is used when we need to decouple an abstraction
 * from its implementation so that the two can vary
 * independently.
 *
 * Bridge is a synonym for the "handle/body" idiom
 *
 * A household switch controlling lights, ceiling fans, etc.
 * is an example of the Bridge.
 *
 * */
public class BridgeApp {

    public static void main(String[] args) {
        Shape s1 = new Rectangle(new RedColor());
        s1.colorIt();
        Shape s2 = new Circle(new BlueColor());
        s2.colorIt();

        // Rectangle filled with red color 
        // Circle filled with blue color
    }
}

abstract class Shape {

    Color color;

    Shape(Color color) {
        this.color = color;
    }

    abstract public void colorIt();
}

class Rectangle extends Shape {

    Rectangle(Color color) {
        super(color);
    }

    public void colorIt() {
        System.out.print("Rectangle filled with ");
        color.fillColor();
    }
}

class Circle extends Shape {

    Circle(Color color) {
        super(color);
    }

    public void colorIt() {
        System.out.print("Circle filled with ");
        color.fillColor();
    }
}

interface Color {

    public void fillColor();
}

class RedColor implements Color {

    public void fillColor() {
        System.out.println("red color");
    }
}

class BlueColor implements Color {

    public void fillColor() {
        System.out.println("blue color");
    }
}
