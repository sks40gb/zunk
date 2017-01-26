package com.sun.designpattern.structural.decorator.b;

/**
 *
 * @author Sunil
 */
public class DecoratorApp {
    public static void main(String[] args) {
        Shape line = new Line();
        line.draw();
        
        Shape decorator = new DottedDecorator(line);
        decorator.draw();
    }
}

interface Shape {

    public void draw();
}

class Line implements Shape {
    public void draw() {
        System.out.println("Drawing Line...");
    }
}

class ShapeDecorator implements Shape{
    private Shape shape;

    public ShapeDecorator(Shape shape) {
        this.shape = shape;
    }
    
    public void draw(){
        shape.draw();
    }
}

class DottedDecorator extends ShapeDecorator{

    public DottedDecorator(Shape shape) {
        super(shape);
    }
    
    public void draw(){
        System.out.println("------------");
        super.draw();
        System.out.println("------------");
    } 
}