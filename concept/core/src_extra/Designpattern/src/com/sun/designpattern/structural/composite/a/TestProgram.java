package com.sun.designpattern.structural.composite.a;

/**
 * In software engineering, the composite pattern is a partitioning design pattern. The composite pattern describes 
 * that a group of objects are to be treated in the same way as a single instance of an object.
 *
 * When dealing with tree-structured data, programmers often have to discriminate between a leaf-node and a branch.
 * This makes code more complex, and therefore, error prone. The solution is an interface that allows treating complex
 * and primitive objects uniformly. In object-oriented programming, a composite is an object designed as a composition
 * of one-or-more similar objects, all exhibiting similar functionality. This is known as a "has-a" relationship
 * between objects[2].
 *
 * When to use :---
 * Composite can be used when clients should ignore the difference between compositions of objects and individual
 * objects.[1] If programmers find that they are using multiple objects in the same way, and often have nearly
 * identical code to handle each of them, then composite is a good choice; it is less complex in this situation to
 * treat primitives and composites as homogeneous.
 */

/** Client */
public class TestProgram {

    public static void main(String[] args) {
        //Initialize four ellipses
        Ellipse ellipse1 = new Ellipse();
        Ellipse ellipse2 = new Ellipse();
        Ellipse ellipse3 = new Ellipse();
        Ellipse ellipse4 = new Ellipse();

        //Initialize three composite graphics
        CompositeGraphic graphic = new CompositeGraphic();
        CompositeGraphic graphic1 = new CompositeGraphic();
        CompositeGraphic graphic2 = new CompositeGraphic();

        //Composes the graphics
        graphic1.add(ellipse1);
        graphic1.add(ellipse2);
        graphic1.add(ellipse3);

        graphic2.add(ellipse4);

        graphic.add(graphic1);
        graphic.add(graphic2);

        //Prints the complete graphic (four times the string "Ellipse").
        graphic.print();
    }
}
