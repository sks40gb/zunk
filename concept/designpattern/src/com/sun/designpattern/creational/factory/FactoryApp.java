package com.sun.designpattern.creational.factory;

/**
 *
 * @author sunil
 */
public class FactoryApp {

    public static void main(String[] args) {
        System.out.println("Shape " + ShapeFactory.getShape("CIRCLE"));
    }

}

interface Shape {

}

class Circle implements Shape {

}

class Rectangle implements Shape {

}

class Square implements Shape {

}

class ShapeFactory {

    //use getShape method to get object of type shape 
    public static Shape getShape(String shapeType) {
        if (shapeType == null) {
            return null;
        }
        if (shapeType.equalsIgnoreCase("CIRCLE")) {
            return new Circle();

        } else if (shapeType.equalsIgnoreCase("RECTANGLE")) {
            return new Rectangle();

        } else if (shapeType.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }

        return null;
    }
}
