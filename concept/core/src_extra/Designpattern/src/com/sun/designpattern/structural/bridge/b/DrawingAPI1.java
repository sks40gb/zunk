/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.designpattern.structural.bridge.b;

/** "ConcreteImplementor" 1/2 */
class DrawingAPI1 implements DrawingAPI {

    public void drawCircle(double x, double y, double radius) {
        System.out.printf("API1.circle at %f:%f radius %f\n", x, y, radius);
    }
}
